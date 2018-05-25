#!/bin/bash

#KC_VERSION=${KC_VERSION:-latest}
KC_VERSION=${KC_VERSION:-3.4.3.Final.3.springio18}

docker build -t tdlabs/keycloak:$KC_VERSION .

docker images -f "dangling=true" -q | xargs --no-run-if-empty docker rmi -f