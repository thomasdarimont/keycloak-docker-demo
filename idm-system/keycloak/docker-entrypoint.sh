#!/bin/bash

if [ $KEYCLOAK_ADMIN_USER ] && [ $KEYCLOAK_ADMIN_PASSWORD ]; then
    keycloak/bin/add-user-keycloak.sh --user $KEYCLOAK_ADMIN_USER --password $KEYCLOAK_ADMIN_PASSWORD
fi

echo "Configuring Keycloak instance with keycloak-setup.cli"

cd $JBOSS_HOME

echo "yes" | bin/jboss-cli.sh --file=./keycloak-setup.cli
echo 
echo "Configuration complete."
echo 
echo "Starting Keycloak"
java -version

export CONTAINER_IP=$(hostname -i)

: "${KC_MIGRATION_ACTION:=import}"
: "${KC_MIGRATION_REALM:=acme}"
: "${KC_MIGRATION_FILE:=acme-realm.json}"

echo Starting with KC_MIGRATION_ACTION="$KC_MIGRATION_ACTION" KC_MIGRATION_REALM=$KC_MIGRATION_REALM KC_MIGRATION_FILE=$KC_MIGRATION_FILE

exec $JBOSS_HOME/bin/standalone.sh \
  -b $CONTAINER_IP \
  -bmanagement $CONTAINER_IP \
  -Djboss.default.multicast.address=224.0.55.55 \
  -Dkeycloak.migration.action=$KC_MIGRATION_ACTION \
  -Dkeycloak.migration.realmName=$KC_MIGRATION_REALM \
  -Dkeycloak.migration.provider=singleFile \
  -Dkeycloak.migration.file=/opt/jboss/keycloak/impexp/$KC_MIGRATION_FILE \
  --debug \
  $@ 
exit $?