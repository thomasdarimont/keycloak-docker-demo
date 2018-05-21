package de.tdlabs.keycloak.ext.events.forwarder;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class JmsEventListenerProviderFactory implements EventListenerProviderFactory {

	private static final Logger LOG = Logger.getLogger(JmsEventListenerProviderFactory.class);

	/**
	 * This ID must not be changed since the extension is registered with that
	 * name in Keycloak.
	 */
	static final String ID = "idm-keycloak-event-listener-jms";

	private static final Set<String> DEFAULT_INCLUDED_CONTEXT_ACTIONS = new HashSet<>(asList( //
			"REGISTER", //
			"REVOKE_ROLE", //
			"GRANT_ROLE", //
			"UPDATE_EMAIL", //
			"UPDATE_PROFILE", //
			"UPDATE_PASSWORD", //
			"UPDATE_TOTP", //
			"REMOVE_TOTP", //
			"DELETE_USER", //
			"CREATE_USER", //
			"RESET_PASSWORD", //
			"MANAGE_USER", //
			"VERIFY_EMAIL" //
	));

	private Set<String> includedContextActions;

	@Override
	public EventListenerProvider create(KeycloakSession keycloakSession) {

		ForwardingEventListenerProvider provider = new ForwardingEventListenerProvider(keycloakSession,
				unmodifiableSet(includedContextActions));
		keycloakSession.enlistForClose(provider);

		return provider;
	}

	@Override
	public void init(Config.Scope config) {

		LOG.infov("Creating IdM Keycloak extension component={0}", InstanceNameHolder.toComponentIdString(this));
		this.includedContextActions = new HashSet<>(DEFAULT_INCLUDED_CONTEXT_ACTIONS);
	}

	@Override
	public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
		// noop
	}

	@Override
	public void close() {

		LOG.infov("Closing IdM Keycloak extension component={0}", InstanceNameHolder.toComponentIdString(this));
		// NOOP
	}

	@Override
	public String getId() {
		return ID;
	}
}