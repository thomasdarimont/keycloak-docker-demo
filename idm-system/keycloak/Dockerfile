FROM jboss/keycloak:4.5.0.Final

# Keycloak User
ENV KEYCLOAK_ADMIN_USER admin
ENV KEYCLOAK_ADMIN_PASSWORD admin

# Logging Properties
ENV LOG_SERVER_URL   udp:graylog
ENV LOG_SERVER_PORT  12123

# JDBC Connection Options
ENV JDBC_URL         JDBC_URL=jdbc:postgresql://sso-db/idm_keycloak_demo
ENV JDBC_USER        keycloak
ENV JDBC_PASSWORD    keycloak
ENV JDBC_DRIVERNAME  postgres

# Active MQ Message Broker
ENV ACTIVE_MQ_USER       idm
ENV ACTIVE_MQ_PASSWORD   idm
ENV ACTIVE_MQ_URL        tcp://sso-amq:61616

ENV KEYCLOAK_EVENT_QUEUE idm.queue.keycloak.rawevents

ENV KEYCLOAK_CONTEXT_PATH u/auth

# JVM Options
ENV JAVA_OPTS        -Xms128M \
                     -Xmx2G \
                     -XX:MetaspaceSize=128M \
                     -XX:MaxMetaspaceSize=256m \
                     -Djava.net.preferIPv4Stack=true \
                     -Djboss.modules.system.pkgs=org.jboss.byteman \
                     -Djava.awt.headless=true \
#                     -Dlogstash-gelf.hostname=$SYSTEM_GROUP-$SYSTEM_COMPONENT-$SYSTEM_PROFILE-$HOSTNAME \
                     # Added  [org.jgroups.protocols.TCP] (TransferQueueBundler...) to remove JGROUPS WARN message like ... no physical address for ..., dropping message
                     -Djava.net.preferIPv4Stack=true

USER root

RUN ln -f -s /usr/share/zoneinfo/Europe/Berlin /etc/localtime

USER jboss

ADD /modules /opt/jboss/keycloak/modules

ADD /themes/springio18 /opt/jboss/keycloak/themes/springio18
ADD /themes/wjax18 /opt/jboss/keycloak/themes/wjax18

COPY activemq-rar.rar /opt/jboss/keycloak
COPY extensions/jms-event-forwarder/target/jms-event-forwarder*.jar /opt/jboss/keycloak/modules/de/tdlabs/keycloak/ext/jms-event-forwarder/main/jms-event-forwarder.jar

COPY extensions/user-storage-provider-demo/ear-module/target/*.ear /opt/jboss/keycloak/standalone/deployments/

COPY docker-entrypoint.sh /opt/jboss 
COPY keycloak-setup.cli /opt/jboss/keycloak

COPY docker-entrypoint.sh /opt/jboss/tools
COPY keycloak-setup.cli /opt/jboss/tools

CMD ["--server-config", "standalone-ha.xml"]