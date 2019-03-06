# Keycloak-Gatekeeper Example

See: https://github.com/keycloak/keycloak-gatekeeper
Documentation: https://www.keycloak.org/docs/latest/securing_apps/index.html#_keycloak_generic_adapter

## Installing Keycloak Gatekeeper
See: https://gist.github.com/thomasdarimont/0c9b22ee9cf40df6306ba3e157052c64


## Setup gatekeeper-client
* Create new client with `client-id` `app-gatekeeper`
* Configure `access-type` as `confidential`
* Standard Flow Enabled `on` everything else `off`
* Configure `Root URL` as http://localhost:8001
* Configure `Valid redirect URI` as `/oauth/callback`
* Configure `Base URL` as `/`
* Copy client secret from `Credentials Tab` and adjust `config.yml`

## Running Keycloak Gatekeeper
```
keycloak-gatekeeper --config ./config.yml
```

### Start a dummy Web Server
The following command uses python's built-in HTTP Server that exposes
the contents of the current working directory on port 8000.

```
# Python 2.7
python -m SimpleHTTPServer

# Python 3.0
python -m http.server
```