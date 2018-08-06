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

exec $JBOSS_HOME/bin/standalone.sh \
  -b $CONTAINER_IP \
  -bmanagement $CONTAINER_IP \
  -Djboss.default.multicast.address=224.0.55.55 \
  -Dkeycloak.migration.action=import \
  -Dkeycloak.migration.realmName=acme \
  -Dkeycloak.migration.provider=singleFile \
  -Dkeycloak.migration.file=acme-realm.json \
  $@ 
exit $?