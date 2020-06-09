package demo.clientauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.OAuth2Constants;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.common.VerificationException;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWKParser;
import org.keycloak.jose.jws.JWSHeader;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.KeysMetadataRepresentation.KeyMetadataRepresentation;

import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class KeycloakClientAuthExample {

    public static void main(String[] args) {

        KeycloakClientFacade facade = new KeycloakClientFacade( //
                "http://localhost:8081/auth" //
                , "admin-client-demo" //
                , "demo-client-1" //
                , "da6947c2-6559-4c37-b219-d37bb72ec2fa" //
        );

        // Get raw AccessToken string for client credentials grant
        System.out.println(facade.getAccessTokenString());

        // Get decoded AccessToken for client credentials grant
//    System.out.println(facade.getAccessToken());

        // Get decoded AccessToken for password credentials grant
        AccessToken accessToken = facade.getAccessToken("tester", "test");
        System.out.println(accessToken.getSubject());
    }

    static class KeycloakClientFacade {

        private final String serverUrl;

        private final String realmId;

        private final String clientId;

        private final String clientSecret;

        public KeycloakClientFacade(String serverUrl, String realmId, String clientId, String clientSecret) {
            this.serverUrl = serverUrl;
            this.realmId = realmId;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public AccessToken getAccessToken() {
            return getAccessToken(newKeycloakBuilderWithClientCredentials().build());
        }

        public String getAccessTokenString() {
            return getAccessTokenString(newKeycloakBuilderWithClientCredentials().build());
        }

        public AccessToken getAccessToken(String username, String password) {
            return getAccessToken(newKeycloakBuilderWithPasswordCredentials(username, password).build());
        }

        public String getAccessTokenString(String username, String password) {
            return getAccessTokenString(newKeycloakBuilderWithPasswordCredentials(username, password).build());
        }

        private AccessToken getAccessToken(Keycloak keycloak) {
            return extractAccessTokenFrom(keycloak, getAccessTokenString(keycloak));
        }

        private String getAccessTokenString(Keycloak keycloak) {
            AccessTokenResponse tokenResponse = getAccessTokenResponse(keycloak);
            return tokenResponse == null ? null : tokenResponse.getToken();
        }

        private AccessToken extractAccessTokenFrom(Keycloak keycloak, String token) {

            if (token == null) {
                return null;
            }

            try {
                TokenVerifier<AccessToken> verifier = TokenVerifier.create(token, AccessToken.class)
                        .withDefaultChecks()
                        .withChecks(new TokenVerifier.RealmUrlCheck(getRealmUrl()));
                PublicKey publicKey = getRealmPublicKey(keycloak, verifier.getHeader());

                return verifier.publicKey(publicKey) //
                        .verify() //
                        .getToken();
            } catch (VerificationException e) {
                return null;
            }
        }

        private KeycloakBuilder newKeycloakBuilderWithPasswordCredentials(String username, String password) {
            return newKeycloakBuilderWithClientCredentials() //
                    .username(username) //
                    .password(password) //
                    .grantType(OAuth2Constants.PASSWORD);
        }

        private KeycloakBuilder newKeycloakBuilderWithClientCredentials() {
            return KeycloakBuilder.builder() //
                    .realm(realmId) //
                    .serverUrl(serverUrl)//
                    .clientId(clientId) //
                    .clientSecret(clientSecret) //
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS);
        }

        private AccessTokenResponse getAccessTokenResponse(Keycloak keycloak) {
            try {
                return keycloak.tokenManager().getAccessToken();
            } catch (Exception ex) {
                return null;
            }
        }

        public String getRealmUrl() {
            return serverUrl + "/realms/" + realmId;
        }

        public String getRealmCertsUrl() {
            return getRealmUrl() + "/protocol/openid-connect/certs";
        }

        private PublicKey getRealmPublicKey(Keycloak keycloak, JWSHeader jwsHeader) {

// Variant 1: use openid-connect /certs endpoint
            return retrievePublicKeyFromCertsEndpoint(jwsHeader);

// Variant 2: use the Public Key referenced by the "kid" in the JWSHeader
// in order to access realm public key we need at least realm role... e.g. view-realm
//      return retrieveActivePublicKeyFromKeysEndpoint(keycloak, jwsHeader);

// Variant 3: use the active RSA Public Key exported by the PublicRealmResource representation
//      return retrieveActivePublicKeyFromPublicRealmEndpoint();
        }

        private PublicKey retrievePublicKeyFromCertsEndpoint(JWSHeader jwsHeader) {

            ObjectMapper om = new ObjectMapper();
            JSONWebKeySet jwks;
            try {
                jwks = om.readValue(new URL(getRealmCertsUrl()).openStream(), JSONWebKeySet.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return Arrays.stream(jwks.getKeys())
                    .filter(key -> jwsHeader.getKeyId().equals(key.getKeyId()))
                    .findAny()
                    .map(jwk -> JWKParser.create(jwk).toPublicKey())
                    .orElse(null);
        }

        private PublicKey retrieveActivePublicKeyFromPublicRealmEndpoint() {

            try {
                ObjectMapper om = new ObjectMapper();
                @SuppressWarnings("unchecked")
                Map<String, Object> realmInfo = om.readValue(new URL(getRealmUrl()).openStream(), Map.class);
                return toPublicKey((String) realmInfo.get("public_key"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private PublicKey retrieveActivePublicKeyFromKeysEndpoint(Keycloak keycloak, JWSHeader jwsHeader) {

            List<KeyMetadataRepresentation> keys = keycloak.realm(realmId).keys().getKeyMetadata().getKeys();

            String publicKeyString = null;
            for (KeyMetadataRepresentation key : keys) {
                if (key.getKid().equals(jwsHeader.getKeyId())) {
                    publicKeyString = key.getPublicKey();
                    break;
                }
            }

            return toPublicKey(publicKeyString);
        }

        public PublicKey toPublicKey(String publicKeyString) {
            try {
                byte[] publicBytes = Base64.getDecoder().decode(publicKeyString);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(keySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                return null;
            }
        }
    }
}