package de.tdlabs.keycloak.ext.events.forwarder;

import lombok.Data;

@Data
public class RoleInfo {
	
	private final String owner;
	private final String type;
	private final String name;
}
