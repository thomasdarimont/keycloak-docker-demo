package de.tdlabs.keycloak.ext.events.forwarder;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Publishes {@link KeycloakIdmEvent IdmEvent's} via JMS Queue.
 */
public class IdmEventPublisher {

	private static final String MESSAGE_TYPE = "messageType";

	private static final String KEYCLOAK_EVENT = "keycloakIdmEvent";

	private static final String JMS_CONNECTION_FACTORY_JNDI_NAME = "java:/jms/ConnectionFactory";

	private static final String EVENT_DESTINATION_JNDI_NAME = "java:/jms/queue/KeyCloakEventQueue";

	private final Destination destination;

	private final ConnectionFactory connectionFactory;

	private final ObjectMapper objectMapper;

	public IdmEventPublisher(ObjectMapper objectMapper) {

		this.objectMapper = objectMapper;

		try {
			Context ctx = new InitialContext();
			this.destination = (Destination) ctx.lookup(EVENT_DESTINATION_JNDI_NAME);
			this.connectionFactory = (ConnectionFactory) ctx.lookup(JMS_CONNECTION_FACTORY_JNDI_NAME);
		} catch (NamingException e) {
			throw new RuntimeException("JMS infrastructure lookup failed: " + e.getMessage(), e);
		}
	}

	boolean isJmsConfigured() {
		return connectionFactory != null && destination != null;
	}

	void publish(KeycloakIdmEvent idmEvent) throws Exception {

		if (!isJmsConfigured()) {
			return;
		}

		try (Connection connection = connectionFactory.createConnection();
				Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
				MessageProducer messageProducer = session.createProducer(destination)) {

			String text = objectMapper.writeValueAsString(idmEvent);

			TextMessage textMessage = session.createTextMessage(text);
			textMessage.setStringProperty(MESSAGE_TYPE, KEYCLOAK_EVENT);

			messageProducer.send(textMessage);
		}
	}
}