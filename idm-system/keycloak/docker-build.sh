#!/bin/bash

KC_VERSION=${KC_VERSION:-latest}

docker build -t tdlabs/keycloak:$KC_VERSION .

docker images -f "dangling=true" -q | xargs --no-run-if-empty docker rmi -f