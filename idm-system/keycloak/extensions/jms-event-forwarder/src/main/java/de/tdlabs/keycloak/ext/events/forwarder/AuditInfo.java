package de.tdlabs.keycloak.ext.events.forwarder;

import lombok.Builder;
import lombok.Data;

/**
 * Audit information about the initiator of the {@link KeycloakIdmEvent IdmEvent}.
 */
@Data
@Builder
public class AuditInfo {

	private final String realmId;
	private final String clientId;
	private final String ipAddress;
	private final String userId;
	private final String username;
}
