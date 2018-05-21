package de.tdlabs.keycloak.ext.events.forwarder;

import java.util.Set;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Reacts on Keycloak {@link Event Event's} and forwards those to the
 * IdM Provisioning System via JMS Message.
 * <p>
 * Uses an {@link IdmEventMapper} to convert Keycloak specific
 * {@link Event Event's} to {@link KeycloakIdmEvent IdmEvent's}.
 * <p>
 * Uses an {@link IdmEventPublisher} to publish the{@link KeycloakIdmEvent} to the IdM.
 */
public class ForwardingEventListenerProvider implements EventListenerProvider {

	private static final Logger LOG = Logger.getLogger(ForwardingEventListenerProvider.class);

	private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

	private final Set<String> forwardedContextActions;

	private KeycloakSession keycloakSession;

	private IdmEventMapper idmEventMapper;

	private IdmEventPublisher idmEventPublisher;

	/**
	 * Creates a new {@link ForwardingEventListenerProvider}
	 * 
	 * @param keycloakSession
	 * @param forwardedContextActions
	 */
	public ForwardingEventListenerProvider(KeycloakSession keycloakSession, Set<String> forwardedContextActions) {

		if (forwardedContextActions == null) {
			throw new NullPointerException("includedKeycloakContextActions must not be null!");
		}

		this.keycloakSession = keycloakSession;
		this.forwardedContextActions = forwardedContextActions;
	}

	private void ensureInitialized() {

		if (this.idmEventMapper != null) {
			return;
		}

		LOG.tracev("Initializing: component={0}", InstanceNameHolder.toComponentIdString(this));

		RealmProvider realmProvider = keycloakSession.getProvider(RealmProvider.class);
		UserProvider userProvider = keycloakSession.getProvider(UserProvider.class);

		this.idmEventMapper = new IdmEventMapper(realmProvider, userProvider, OBJECT_MAPPER);
		this.idmEventPublisher = new IdmEventPublisher(OBJECT_MAPPER);
	}

	@Override
	public void onEvent(Event event) {

		if (event.getUserId() == null) {
			// only propagate user events
			return;
		}

		ensureInitialized();

		if (!idmEventPublisher.isJmsConfigured()) {
			return;
		}

		KeycloakIdmEvent idmEvent = idmEventMapper.toIdmEvent(event);

		LOG.debugv("Handle {1} Event: EventId={0} Type={1} User={2} ContextAction={3}", idmEvent.getEventId(),
				idmEvent.getType(), idmEvent.getUserId(), idmEvent.getContextAction());

		tryPublishEvent(idmEvent);
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {

		ensureInitialized();

		if (!idmEventPublisher.isJmsConfigured()) {
			return;
		}

		LOG.debugv("Handle raw ADMIN Event: Type: <{0}> Resource: <{1}>", event.getOperationType(),
				event.getResourcePath());
		KeycloakIdmEvent idmEvent = idmEventMapper.toIdmEvent(event);

		LOG.debugv("Handle {1} Event: EventId={0} Type={1} User={2} ContextAction={3}", idmEvent.getEventId(),
				idmEvent.getType(), idmEvent.getUserId(), idmEvent.getContextAction());

		tryPublishEvent(idmEvent);
	}

	private void tryPublishEvent(KeycloakIdmEvent idmEvent) {

		if (!forwardedContextActions.contains(idmEvent.getContextAction())) {
			LOG.debugv(
					"Skipping Forwarding {1} Event: EventId={0} Type={1} User={2} ContextAction={3} - Context Action was not white-listed.",
					idmEvent.getEventId(), idmEvent.getType(), idmEvent.getUserId(), idmEvent.getContextAction());
			return;
		}

		try {
			LOG.debugv("Begin Forwarding {1} Event: EventId={0} Type={1} User={2} ContextAction={3}",
					idmEvent.getEventId(), idmEvent.getType(), idmEvent.getUserId(), idmEvent.getContextAction());
			idmEventPublisher.publish(idmEvent);
			LOG.debugv("End Forwarding {1} Event: EventId={0} Type={1} User={2} ContextAction={3}",
					idmEvent.getEventId(), idmEvent.getType(), idmEvent.getUserId(), idmEvent.getContextAction());
			LOG.infov("Forwarded {1} Event: EventId={0} Type={1} User={2} ContextAction={3}",
					idmEvent.getEventId(), idmEvent.getType(), idmEvent.getUserId(), idmEvent.getContextAction());
		} catch (Exception e) {
			LOG.warnv(e, "Failed Forwarding {1} Event: EventId={0} Type={1} User={2} ContextAction={3}",
					idmEvent.getEventId(), idmEvent.getType(), idmEvent.getUserId(), idmEvent.getContextAction());
		}
	}

	@Override
	public void close() {

		LOG.tracev("Closing: component={0}", InstanceNameHolder.toComponentIdString(this));

		if (idmEventMapper != null) {
			LOG.tracev("Closing: component={0}", InstanceNameHolder.toComponentIdString(idmEventMapper));
			idmEventMapper.close();
			LOG.tracev("Closed: component={0}", InstanceNameHolder.toComponentIdString(idmEventMapper));
		}
	}

	private static ObjectMapper createObjectMapper() {

		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT);
		om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		return om;
	}
}