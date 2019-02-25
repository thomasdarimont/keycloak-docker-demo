#!/bin/bash

#KC_VERSION=${KC_VERSION:-latest}
KC_VERSION=${KC_VERSION:-4.8.3.Final.1.demo}

docker build -t tdlabs/keycloak:$KC_VERSION .

docker images -f "dangling=true" -q | xargs --no-run-if-empty docker rmi -f
