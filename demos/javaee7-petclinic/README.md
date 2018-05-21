# Java EE 7 Petclinic #

JSF with Java EE 7 Version of Spring Petclinic

Running on OpenShift: http://javaee7petclinic-port80guru.rhcloud.com/

Blog: http://thomas-woehlke.blogspot.de/2014/02/java-ee-7-petclinic.html

## install JBoss Wildfly ##

install JBoss Wildfly 8.2.1.Final from http://wildfly.org/downloads/ to e.g. /Users/tw/srv/wildfly-8.2.1.Final/

start JBoss by: cd /Users/tw/srv/wildfly-8.2.1.Final/bin ; ./standalone.sh

## install Glassfish 4 ##

I installed Netbeans 8.0.2 with Glassfish 4.1 from https://netbeans.org/downloads/

starting on Mac OSX:

cd /Applications/NetBeans/glassfish-4.1/bin/

sudo ./asadmin start-database

sudo ./asadmin start-domain

For Starting the JavaDB Database with jdk1.7.0_51 refer to: http://thomas-woehlke.blogspot.de/2014/01/start-glassfish4-javadb-running-jdk17051.html

## Functional Tests with Selenium2 Webdriver, Arquillian Drone and Graphene ##

Samples are tested on Wildfly and GlassFish using the Arquillian ecosystem.

Only one profile can be active at a given time otherwise there will be dependency conflicts.

* ``mvn clean install -Pwildfly-managed``
    This profile  will install a Wildfly server and start up the server.
    Useful for CI servers.

* ``mvn clean install -Pwildfly-remote``
    This profile requires you to start up a Wildfly server outside of the build.
    Useful for development to avoid the server start up cost per sample.

* ``mvn clean install -Pglassfish-remote``
    This profile requires you to start up a GlassFish 4 server outside of the build. Each sample will then
    reuse this instance to run the tests.
    Useful for development to avoid the server start up cost per test.


* ``mvn clean install -Pglassfish-managed``
    This profile  will install a Glassfish 4 server and start up the server.
    Useful for development, but has the downside of server startup per Test.
    You have to start a JavaDB (Derby) Server outside of the build before running the Test.

When developing and runing them from IDE, remember to activate the profile before running the test.

To learn more about Arquillian please refer to the [Arquillian Guides](http://arquillian.org/guides/)

## build and run ##

git clone https://github.com/phasenraum2010/javaee7-petclinic.git

build project with: ``mvn clean install wildfly:deploy``

open url in browser: http://localhost:8080/javaee7-petclinic-1.3-SNAPSHOT/

## openshift ##

The OpenShift `jbossas` cartridge documentation can be found at:

https://github.com/openshift/origin-server/tree/master/cartridges/openshift-origin-cartridge-jbossas/README.md

## First Steps ##

add some PetTypes like dog,cat,mouse,...

add some Specialties for Vetinarians like dentist, anesthetist, radiology,...

add a Vetinarian

add an Owner, add him am a Pet and his Pet a visit.

## visit Spring Petclinic ##

https://github.com/spring-projects/spring-petclinic
