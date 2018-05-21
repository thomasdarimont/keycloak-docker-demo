package de.tdlabs.keycloak.ext.events.forwarder;

import static java.util.Arrays.asList;
import static java.util.regex.Pattern.compile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.tdlabs.keycloak.ext.events.forwarder.AuditInfo.AuditInfoBuilder;
import de.tdlabs.keycloak.ext.events.forwarder.UserInfo.UserInfoBuilder;
import lombok.Getter;

/**
 * Maps Keycloak internal Events ({@link Event} {@link AdminEvent}) into
 * uniform {@link KeycloakIdmEvent IdmEvent's}.
 * 
 * {@link AdminEvent AdminEvent's} types are identified by their REST Resource Path.
 */
class IdmEventMapper implements AutoCloseable {

	private static final Logger LOG = Logger.getLogger(IdmEventMapper.class);

	/**
	 * Holds a {@link MatchableIdmEventEnricherProvider} for Keycloak User
	 * {@link Event Event's}.
	 * <p>
	 * Order is relevant, since the first matching EnricherProvider will be used.
	 */
	private static final List<MatchableIdmEventEnricherProvider> USER_EVENT_ENRICHER = asList( //
			new UserEventEnricherProvider(), //

			new UnknownUserEventEnricherProvider() //
	);

	/**
	 * Holds a {@link MatchableIdmEventEnricherProvider} for Keycloak
	 * {@link AdminEvent AdminEvent's}.
	 * 
	 * <p>
	 * Order is relevant, since the first matching EnricherProvider will be used.
	 */
	private static final List<MatchableIdmEventEnricherProvider> ADMIN_EVENT_ENRICHER = asList( //
			new UserAdminEventEnricherProvider(), //
			new ClientRoleMappingAdminEventEnricherProvider(), //
			new RealmRoleMappingAdminEventEnricherProvider(), //
			new UserGroupAdminEventEnricherProvider(), //
			new UserPasswordAdminEventEnricherProvider(), //

			new UnknownAdminEventEnricherProvider() //
	);

	private static Map<KeycloakIdmEvent.Type, List<MatchableIdmEventEnricherProvider>> EVENT_ENRICHER_MAP;

	static {
		Map<KeycloakIdmEvent.Type, List<MatchableIdmEventEnricherProvider>> map = new HashMap<>();
		map.put(KeycloakIdmEvent.Type.ADMIN, ADMIN_EVENT_ENRICHER);
		map.put(KeycloakIdmEvent.Type.USER, USER_EVENT_ENRICHER);
		EVENT_ENRICHER_MAP = map;
	}

	private final RealmProvider realmProvider;

	private final UserProvider userProvider;

	private final ObjectMapper objectMapper;

	IdmEventMapper(RealmProvider realmProvider, UserProvider userProvider, ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.realmProvider = realmProvider;
		this.userProvider = userProvider;
	}

	KeycloakIdmEvent toIdmEvent(AdminEvent adminEvent) {
		return extractIdmEventFrom(new KeycloakEventAdapter(adminEvent));
	}

	KeycloakIdmEvent toIdmEvent(Event event) {
		return extractIdmEventFrom(new KeycloakEventAdapter(event));
	}

	private KeycloakIdmEvent extractIdmEventFrom(KeycloakEventAdapter eventAdapter) {

		KeycloakIdmEvent idmEvent = eventAdapter.getIdmEvent();

		findFirstMatchingEventEnricher(eventAdapter).ifPresent((enricher) -> {
			enricher.enrich(idmEvent);
		});

		idmEvent.setInstanceName(InstanceNameHolder.INSTANCE_NAME);
		idmEvent.setAuditInfo(createAuditInfo(eventAdapter));
		idmEvent.setUserInfo(createUserInfo(idmEvent.getRealmId(), idmEvent.getUserId()));

		return idmEvent;
	}

	private Optional<IdmEventEnricher> findFirstMatchingEventEnricher(KeycloakEventAdapter eventAdapter) {

		MatchingContext matchingContext = new MatchingContext(eventAdapter);

		for (MatchableIdmEventEnricherProvider enricherProvider : EVENT_ENRICHER_MAP.get(eventAdapter.getType())) {

			IdmEventEnricher enricher = enricherProvider.returnEventEnricherIfEventPatternMatches(eventAdapter,
					matchingContext);
			if (enricher != null) {
				return Optional.of(enricher);
			}
		}

		return Optional.empty();
	}

	public void close() {

		if (realmProvider != null) {
			realmProvider.close();
		}

		if (userProvider != null) {
			userProvider.close();
		}
	}

	private AuditInfo createAuditInfo(KeycloakEventAdapter eventAdapter) {

		if (eventAdapter.isAdminEvent()) {
			return extractAuditInfo(eventAdapter.getAdminEvent());
		}

		return extractAuditInfo(eventAdapter.getUserEvent());
	}

	private AuditInfo extractAuditInfo(AdminEvent adminEvent) {

		AuthDetails authDetails = adminEvent.getAuthDetails();

		String realmId = authDetails.getRealmId();
		String userId = authDetails.getUserId();

		AuditInfoBuilder authInfoBuilder = AuditInfo.builder() //
				.clientId(authDetails.getClientId()) //
				.realmId(realmId) //
				.ipAddress(authDetails.getIpAddress()) //
				.userId(userId); //

		UserModel user = lookupRealmUser(realmId, userId);

		if (user != null) {
			authInfoBuilder.username(user.getUsername());
		}

		return authInfoBuilder.build();
	}

	private AuditInfo extractAuditInfo(Event event) {

		String realmId = event.getRealmId();
		String userId = event.getUserId();

		AuditInfoBuilder authInfoBuilder = AuditInfo.builder() //
				.clientId(event.getClientId()) //
				.realmId(realmId) //
				.ipAddress(event.getIpAddress()) //
				.userId(userId); //

		UserModel user = lookupRealmUser(realmId, userId);

		if (user != null) {
			authInfoBuilder.username(user.getUsername());
		}

		return authInfoBuilder.build();
	}

	private UserModel lookupRealmUser(String realmId, String userId) {

		RealmModel realm = realmProvider.getRealm(realmId);
		UserModel user = userProvider.getUserById(userId, realm);

		return user;
	}

	private UserInfo createUserInfo(String realmId, String userId) {

		if (userId == null) {
			return null;
		}

		UserModel user = lookupRealmUser(realmId, userId);

		UserInfoBuilder userInfoBuilder = UserInfo.builder() //
				.userId(userId) //
				.realmId(realmId);

		if (user != null) {
			userInfoBuilder = userInfoBuilder //
					.emailVerified(user.isEmailVerified()) //
					.enabled(user.isEnabled()) //
					.username(user.getUsername()) //
					.email(user.getEmail()) //
					.firstname(user.getFirstName()) //
					.lastname(user.getLastName()) //
					.attributes(user.getAttributes()) //
					.creationDateTime(user.getCreatedTimestamp()) //
			;
		}

		return userInfoBuilder.build();
	}

	interface MatchableIdmEventEnricherProvider {

		String UUID_PATTERN_STRING = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

		String USER_CONTEXT = "USER";
		String ROLE_CONTEXT = "ROLE";
		String GROUP_CONTEXT = "GROUP";

		/**
		 * Returns an {@link IdmEventEnricher} if the Event encapsulated by {@link KeycloakEventAdapter}
		 * matches the Pattern else {@literal null}.
		 * 
		 * @param eventAdapter
		 * @param matchingContext
		 * @return
		 */
		IdmEventEnricher returnEventEnricherIfEventPatternMatches(KeycloakEventAdapter eventAdapter,
                                                                  MatchingContext matchingContext);
	}

	static abstract class AbstractMatchableUserEventEnricherProvider implements MatchableIdmEventEnricherProvider {

		public IdmEventEnricher returnEventEnricherIfEventPatternMatches(KeycloakEventAdapter eventAdapter,
				MatchingContext matchingContext) {

			if (!eventAdapter.isUserEvent()) {
				return null;
			}

			return returnEventEnricher(eventAdapter.getUserEvent(), matchingContext);
		}

		/**
		 * Returns an {@link IdmEventEnricher} which can enrich a
		 * {@link KeycloakIdmEvent} with information from the Keycloak
		 * {@link Event} bzw. {@link AdminEvent}.
		 * 
		 * @param event
		 * @param matchingContext
		 * @return
		 */
		abstract IdmEventEnricher returnEventEnricher(Event event, MatchingContext matchingContext);
	}

	static class UserEventEnricherProvider extends AbstractMatchableUserEventEnricherProvider {

		@Override
		IdmEventEnricher returnEventEnricher(Event event, MatchingContext matchingContext) {

			return (idmEvent) -> {
				idmEvent.setContextId(USER_CONTEXT);
				idmEvent.setContextAction(event.getType().name());
			};
		}
	}

	static class UnknownUserEventEnricherProvider extends AbstractMatchableUserEventEnricherProvider {

		private static final String UNSPECIFIED_USER_EVENT = "UNSPECIFIED_USER_EVENT";

		@Override
		IdmEventEnricher returnEventEnricher(Event event, MatchingContext matchingContext) {

			return (idmEvent) -> {
				idmEvent.setContextId(UNSPECIFIED_USER_EVENT);
				idmEvent.setContextAction(event.getType().name());
			};

		}
	}

	static abstract class AbstractMatchableAdminEventEnricherProvider implements MatchableIdmEventEnricherProvider {

		private final Pattern pattern;

		AbstractMatchableAdminEventEnricherProvider(Pattern pattern) {
			this.pattern = pattern;
		}

		@Override
		public IdmEventEnricher returnEventEnricherIfEventPatternMatches(KeycloakEventAdapter eventAdapter,
				MatchingContext matchingContext) {

			if (!eventAdapter.isAdminEvent()) {
				return null;
			}

			AdminEvent adminEvent = eventAdapter.getAdminEvent();
			String resourcePath = adminEvent.getResourcePath();

			if (resourcePath == null) {
				return null;
			}

			Matcher matcher = pattern.matcher(resourcePath);

			if (!matcher.matches()) {
				return null;
			}

			return returnEventEnricher(adminEvent, matcher, matchingContext);
		}

		abstract IdmEventEnricher returnEventEnricher(AdminEvent adminEvent, Matcher matcher,
				MatchingContext matchingContext);
	}

	static class UnknownAdminEventEnricherProvider extends AbstractMatchableAdminEventEnricherProvider {

		private static final String UNSPECIFIED_ADMIN_EVENT = "UNSPECIFIED_ADMIN_EVENT";

		UnknownAdminEventEnricherProvider() {
			super(compile(".*"));
		}

		@Override
		IdmEventEnricher returnEventEnricher(AdminEvent adminEvent, Matcher matcher, MatchingContext matchingContext) {

			return (idmEvent) -> {

				// We don't have any forther information about the event
				idmEvent.setContextAction(UNSPECIFIED_ADMIN_EVENT);
				idmEvent.setContextId(UNSPECIFIED_ADMIN_EVENT);
				// Record Resource Path und Representation for later analysis.
				idmEvent.getContextData().put("representation", adminEvent.getRepresentation());
				idmEvent.getContextData().put("resourcePath", matcher.group(0));
			};
		}
	}

	static abstract class AbstractMatchableRoleMappingAdminEventEnricherProvider
			extends AbstractMatchableAdminEventEnricherProvider {

		static final String ROLES_ATTRIBUTE_NAME = "roles";

		private static final String GRANT_ROLE_ACTION = "GRANT_ROLE";

		private static final String REVOKE_ROLE_ACTION = "REVOKE_ROLE";

		static final String CLIENT_ROLE = "CLIENT_ROLE";

		static final String REALM_ROLE = "REALM_ROLE";

		AbstractMatchableRoleMappingAdminEventEnricherProvider(Pattern pattern) {
			super(pattern);
		}

		KeycloakIdmEvent addRoleMappingEventDataToIdmEvent(KeycloakIdmEvent idmEvent, AdminEvent adminEvent,
				String roleType, String roleOwnerId, ObjectMapper objectMapper) {

			idmEvent.setContextId(ROLE_CONTEXT);
			idmEvent.setContextAction(tryNarrowRoleContextAction(adminEvent));
			idmEvent.getContextData().put(ROLES_ATTRIBUTE_NAME,
					extractRoleInfos(adminEvent, roleType, roleOwnerId, objectMapper));

			return idmEvent;
		}

		private String tryNarrowRoleContextAction(AdminEvent adminEvent) {

			switch (adminEvent.getOperationType()) {
			case CREATE:
				return GRANT_ROLE_ACTION;
			case DELETE:
				return REVOKE_ROLE_ACTION;
			default:
				return adminEvent.getOperationType().name();
			}
		}

		private List<RoleInfo> extractRoleInfos(AdminEvent adminEvent, String roleType, String roleOwnerId,
				ObjectMapper objectMapper) {

			List<RoleInfo> roleInfos = new ArrayList<>();

			try {
				List<Map<String, Object>> roles = objectMapper.readValue(adminEvent.getRepresentation(),
						new TypeReference<ArrayList<LinkedHashMap<String, Object>>>() {
						});

				for (Map<String, Object> role : roles) {
					roleInfos.add(new RoleInfo(roleOwnerId, roleType, (String) role.get("name")));
				}

			} catch (Exception e) {
				LOG.warnv(e, "Couldn't extract role infos from admin event <{0}>", adminEvent);
			}

			return roleInfos;
		}
	}

	static class ClientRoleMappingAdminEventEnricherProvider
			extends AbstractMatchableRoleMappingAdminEventEnricherProvider {

		ClientRoleMappingAdminEventEnricherProvider() {
			super(compile("^users/(" + UUID_PATTERN_STRING + ")/role-mappings/clients/(" + UUID_PATTERN_STRING + ")$"));
		}

		@Override
		IdmEventEnricher returnEventEnricher(AdminEvent adminEvent, Matcher matcher, MatchingContext matchingContext) {

			String userId = matcher.group(1);
			String clientId = matcher.group(2);

			return (idmEvent) -> {

				idmEvent.setUserId(userId);

				String clientClientId = matchingContext.getRealmModel().getClientById(clientId).getClientId();
				addRoleMappingEventDataToIdmEvent(idmEvent, adminEvent, CLIENT_ROLE, clientClientId,
						matchingContext.getObjectMapper());
			};
		}

	}

	static class RealmRoleMappingAdminEventEnricherProvider
			extends AbstractMatchableRoleMappingAdminEventEnricherProvider {

		RealmRoleMappingAdminEventEnricherProvider() {
			super(compile("^users/(" + UUID_PATTERN_STRING + ")/role-mappings/realm/?(" + UUID_PATTERN_STRING + ")?$"));
		}

		@Override
		IdmEventEnricher returnEventEnricher(AdminEvent adminEvent, Matcher matcher, MatchingContext matchingContext) {

			String userId = matcher.group(1);
			// ignored String roleId = realmRoleMappingMatcher.group(2);

			return (idmEvent) -> {

				idmEvent.setUserId(userId);

				addRoleMappingEventDataToIdmEvent(idmEvent, adminEvent, REALM_ROLE, adminEvent.getRealmId(),
						matchingContext.getObjectMapper());
			};
		}
	}

	static class UserAdminEventEnricherProvider extends AbstractMatchableAdminEventEnricherProvider {

		private static final String MANAGE_USER_ACTION = "MANAGE_USER";
		private static final String CREATE_USER_ACTION = "CREATE_USER";
		private static final String DELETE_USER_ACTION = "DELETE_USER";

		UserAdminEventEnricherProvider() {
			super(compile("^users/(" + UUID_PATTERN_STRING + ")$"));
		}

		@Override
		IdmEventEnricher returnEventEnricher(AdminEvent adminEvent, Matcher matcher, MatchingContext matchingContext) {

			String userId = matcher.group(1);

			return (idmEvent) -> {

				idmEvent.setUserId(userId);
				idmEvent.setContextId(USER_CONTEXT);
				idmEvent.setContextAction(tryNarrowUserContextAction(adminEvent));
			};
		}

		private String tryNarrowUserContextAction(AdminEvent adminEvent) {

			if (adminEvent == null) {
				return MANAGE_USER_ACTION;
			}

			switch (adminEvent.getOperationType()) {
			case CREATE:
				return CREATE_USER_ACTION;
			case DELETE:
				return DELETE_USER_ACTION;
			default:
				return MANAGE_USER_ACTION;
			}
		}
	}

	static class UserPasswordAdminEventEnricherProvider extends AbstractMatchableAdminEventEnricherProvider {

		private static final String RESET_PASSWORD_ACTION = "RESET_PASSWORD";

		UserPasswordAdminEventEnricherProvider() {
			super(compile("^users/(" + UUID_PATTERN_STRING + ")/reset-password$"));
		}

		@Override
		IdmEventEnricher returnEventEnricher(AdminEvent adminEvent, Matcher matcher, MatchingContext matchingContext) {

			String userId = matcher.group(1);

			return (idmEvent) -> {

				idmEvent.setUserId(userId);
				idmEvent.setContextId(USER_CONTEXT);
				idmEvent.setContextAction(RESET_PASSWORD_ACTION);
			};
		}
	}

	static class UserGroupAdminEventEnricherProvider extends AbstractMatchableRoleMappingAdminEventEnricherProvider {

		private static final String JOIN_GROUP_ACTION = "JOIN_GROUP";
		private static final String LEAVE_GROUP_ACTION = "LEAVE_GROUP";

		UserGroupAdminEventEnricherProvider() {
			super(compile("^users/(" + UUID_PATTERN_STRING + ")/groups/(" + UUID_PATTERN_STRING + ")$"));
		}

		@Override
		IdmEventEnricher returnEventEnricher(AdminEvent adminEvent, Matcher matcher, MatchingContext matchingContext) {

			return (idmEvent) -> {

				String userId = matcher.group(1);
				String groupId = matcher.group(2);
				idmEvent.setUserId(userId);

				addGroupMembershipChangeEventDataToIdmEvent(idmEvent, adminEvent, userId, groupId,
						matchingContext.getObjectMapper());
			};
		}

		@SuppressWarnings("unchecked")
		private void addGroupMembershipChangeEventDataToIdmEvent(KeycloakIdmEvent idmEvent, AdminEvent adminEvent,
				String userId, String groupId, ObjectMapper objectMapper) {

			idmEvent.setContextId(GROUP_CONTEXT);
			idmEvent.setContextAction(tryNarrowGroupContextAction(adminEvent));

			try {
				Map<String, Object> groupMembership = objectMapper.readValue(adminEvent.getRepresentation(),
						new TypeReference<LinkedHashMap<String, Object>>() {
						});

				List<String> realmRoleNames = (List<String>) groupMembership.get("realmRoles");
				Map<String, List<String>> clientRoles = (Map<String, List<String>>) groupMembership.get("clientRoles");

				idmEvent.getContextData().put(ROLES_ATTRIBUTE_NAME,
						extractRoleInfosFromGroupMembershipChange(adminEvent, realmRoleNames, clientRoles));
			} catch (Exception e) {
				LOG.warnv(e, "Couldn't extract group infos from admin event <{0}>", adminEvent);
			}
		}

		private String tryNarrowGroupContextAction(AdminEvent adminEvent) {

			switch (adminEvent.getOperationType()) {
			case CREATE:
				return JOIN_GROUP_ACTION;
			case DELETE:
				return LEAVE_GROUP_ACTION;
			default:
				return adminEvent.getOperationType().name();
			}
		}

		private List<RoleInfo> extractRoleInfosFromGroupMembershipChange(AdminEvent adminEvent,
				List<String> realmRoleNames, Map<String, List<String>> clientRoles) {

			List<RoleInfo> roleInfos = new ArrayList<>();

			for (String roleName : realmRoleNames) {
				roleInfos.add(new RoleInfo(adminEvent.getRealmId(), REALM_ROLE, roleName));
			}

			for (Map.Entry<String, List<String>> clientRoleEntry : clientRoles.entrySet()) {

				for (String roleName : clientRoleEntry.getValue()) {
					roleInfos.add(new RoleInfo(clientRoleEntry.getKey(), CLIENT_ROLE, roleName));
				}
			}

			return roleInfos;
		}
	}

	@Getter
	class MatchingContext {

		private final RealmModel realmModel;

		MatchingContext(KeycloakEventAdapter eventAdapter) {
			this.realmModel = realmProvider.getRealm(eventAdapter.getRealmId());
		}

		ObjectMapper getObjectMapper() {
			return objectMapper;
		}
	}

	/**
	 * Functional Interface to enrich {@link KeycloakIdmEvent
	 * IdmEvent's} with additional data.
	 * 
	 * @author tdarimont
	 */
	interface IdmEventEnricher {

		void enrich(KeycloakIdmEvent idmEvent);
	}

	/**
	 * Provides uniform access to Keycloak {@link Event} und
	 * {@link AdminEvent}.
	 * 
	 * @author tdarimont
	 */
	static class KeycloakEventAdapter {

		@Getter
		private final AdminEvent adminEvent;

		@Getter
		private final Event userEvent;

		@Getter
		private final KeycloakIdmEvent idmEvent;

		@Getter
		private final KeycloakIdmEvent.Type type;

		public KeycloakEventAdapter(AdminEvent adminEvent) {
			this(adminEvent, null, KeycloakIdmEvent.Type.ADMIN);
		}

		public KeycloakEventAdapter(Event userEvent) {
			this(null, userEvent, KeycloakIdmEvent.Type.USER);
		}

		private KeycloakEventAdapter(AdminEvent adminEvent, Event userEvent, KeycloakIdmEvent.Type type) {

			this.adminEvent = adminEvent;
			this.userEvent = userEvent;
			this.type = type;
			this.idmEvent = adminEvent != null ? new KeycloakIdmEvent(adminEvent) : new KeycloakIdmEvent(userEvent);
		}

		public boolean isUserEvent() {
			return userEvent != null;
		}

		public boolean isAdminEvent() {
			return adminEvent != null;
		}

		public String getRealmId() {

			if (adminEvent != null) {
				return adminEvent.getRealmId();
			}

			return userEvent.getRealmId();
		}
	}
}