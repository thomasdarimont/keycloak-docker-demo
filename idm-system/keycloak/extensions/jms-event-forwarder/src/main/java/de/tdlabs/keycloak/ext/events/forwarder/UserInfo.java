package de.tdlabs.keycloak.ext.events.forwarder;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {

	private final String userId;
	private final String realmId;
	private final boolean emailVerified;
	private final boolean enabled;
	private final String username;
	private final String email;
	private final String firstname;
	private final String lastname;
	private final long creationDateTime;
	private final Map<String, List<String>> attributes;
}
