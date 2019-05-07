# Example Backend Service that acts as resource server without Keycloak libraries

This example demonstrates a Keycloak  integration with plain spring-security oauth2. 

```
KC_CLIENT_ID=app-cli
KC_ISSUER=http://sso.tdlabs.local:8899/u/auth/realms/acme
KC_USERNAME=tester
KC_PASSWORD=test

KC_RESPONSE=$( \
curl \
-d "client_id=$KC_CLIENT_ID" \
-d "username=$KC_USERNAME" \
-d "password=$KC_PASSWORD" \
-d "grant_type=password" \
"$KC_ISSUER/protocol/openid-connect/token" \
)

echo $KC_RESPONSE | jq -C .

KC_ACCESS_TOKEN=$(echo $KC_RESPONSE | jq -r .access_token)


curl -v -H "Authorization: Bearer $KC_ACCESS_TOKEN" http://apps.tdlabs.local:20009/api/greetings/user

curl -v -H "Authorization: Bearer $KC_ACCESS_TOKEN" http://apps.tdlabs.local:20009/api/resources/42

```