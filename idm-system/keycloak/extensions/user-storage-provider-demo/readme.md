# A custom Keycloak User Storage Provider

This example demonstrates how to deploy custom Keycloak User storage provider as an `.ear`. 
This allows to use custom dependencies that are not part of the keycloak module space.  

The storage provider is implemented in the `jar-module` project.

This example is based on [keycloak-user-spi-demo](https://github.com/dasniko/keycloak-user-spi-demo) by [@dasniko](https://github.com/dasniko).

## Prepare
    KEYCLOAK_HOME=/home/tom/dev/playground/keycloak/keycloak-3.3.0.CR1

## Build
Build the ear archive

    mvn clean install

## Prepare Keycloak
Run [setup.cli](./setup.cli) with

    echo "yes" | $KEYCLOAK_HOME/bin/jboss-cli.sh --file=$KEYCLOAK_HOME/setup.cli
This configures the custom logger and registers the static configuration 
of the user federation provider. 

## Start Keycloak
    
    cd $KEYCLOAK_HOME
    bin/standalone.sh -c standalone-ha.xml 

## Deployment
Deploy `.ear` to wildfly

    mvn wildfly:deploy -Djboss-as.home=$KEYCLOAK_HOME

This copies the `.ear` file to `standalone/deployments` folder.