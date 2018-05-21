package de.tdlabs.keycloak.ext.events.forwarder;

import static java.util.Collections.emptyMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

import lombok.Data;

/**
 * Unifies Keycloak {@link Event Event's} und {@link AdminEvent
 * AdminEvent's}.
 */
@Data
public class KeycloakIdmEvent {

	/**
	 * {@link UUID} ID of the Event as {@link String}.
	 */
	private String eventId;
	
	/**
	 * Name of the Keycloak Instanz which created the event.
	 */
	private String instanceName;

	/**
	 * Technical name of the Realm.
	 */
	private String realmId;

	/**
	 * {@link UUID} ID of the User as {@link String}.
	 */
	private String userId;

	/**
	 * IdmEvent {@link Type}.
	 */
	private Type type;

	/**
	 * UNIX timestamp of the Event.
	 */
	private long timestamp;

	/**
	 * Context in which the event occured.
	 */
	private String contextId;

	/**
	 * Context action which triggered the {@link KeycloakIdmEvent}.
	 */
	private String contextAction;

	/**
	 * Additional context data
	 */
	private Map<String, Object> contextData = emptyMap();

	/**
	 * Holds audit information about who triggered the event creation.
	 */
	private AuditInfo auditInfo;

	/**
	 * Holds information about the user the event was created for.
	 */
	private UserInfo userInfo;

	public KeycloakIdmEvent(AdminEvent adminEvent) {
		this(null, adminEvent.getRealmId(), adminEvent.getTime(), Type.ADMIN);
	}

	public KeycloakIdmEvent(Event userEvent) {
		this(userEvent.getUserId(), userEvent.getRealmId(), userEvent.getTime(), Type.USER);
	}

	private KeycloakIdmEvent(String userId, String realmId, long timestamp, Type type) {

		this.eventId = UUID.randomUUID().toString();
		this.userId = userId;
		this.realmId = realmId;
		this.timestamp = timestamp;
		this.type = type;
		this.contextData = new LinkedHashMap<>();
	}

	/**
	 * Typ of the Keycloak event.
	 * 
	 * @author tdarimont
	 */
	enum Type {
		/**
		 * {@code USER USER Event's} are Keycloak Events created for user interaction.
		 */
		USER,

		/**
		 * {@code ADMIN AdminEvent's} are Keycloak Events created for admin interaction.
		 */
		ADMIN
	}
}
