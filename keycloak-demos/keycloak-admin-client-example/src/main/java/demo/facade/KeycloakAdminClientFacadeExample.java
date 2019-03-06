package demo.facade;

import lombok.Data;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class KeycloakAdminClientFacadeExample {

    public static void main(String[] args) {

        String serverUrl = "http://sso.tdlabs.local:8899/u/auth";
        String realm = "acme";
        String clientId = "idm-client";
        String clientSecret = "0d61686d-57fc-4048-b052-4ce74978c468";

        KeycloakFacade facade = DefaultKeycloakFacade.builder() //
                .setServerUrl(serverUrl) //
                .setRealmId(realm) //
                // service account with manage-users role
                .setClientId(clientId) //
                .setClientSecret(clientSecret) //
                .build();


        // Note: one could also manage other realms with an admin-user in the master realm
        String targetRealm = realm;

        facade.findUserById(targetRealm, "0fd471f7-9b43-4e68-aff7-8a57284019e3").ifPresent(u -> System.out.println(u));
    }

    interface KeycloakFacade {

        long getUserCount(String realmId);

        List<UserInfo> listAllUsers(String realmId);

        Optional<UserInfo> findUserById(String realmId, String userId);

        UserReference createUser(String realmId, UserInfo userInfo);

        void deleteUser(String realmId, UserReference userReference);

        void forEachFoundUser(String realmId, String search, int batchSize, Consumer<UserInfo> consumer);
    }

    static class DefaultKeycloakFacade implements KeycloakFacade {

        private final Keycloak keycloak;

        public DefaultKeycloakFacade(Keycloak keycloak) {
            this.keycloak = keycloak;
        }

        @Override
        public Optional<UserInfo> findUserById(String realmId, String userId) {
            try {
                return Optional.of(getUsersResource(realmId).get(userId).toRepresentation()).map(DefaultKeycloakFacade::toUserInfo);
            } catch (ProcessingException pe) {
                return Optional.empty();
            }
        }

        @Override
        public long getUserCount(String realmId) {
            return getUsersResource(realmId).count();
        }

        @Override
        public List<UserInfo> listAllUsers(String realmId) {

            List<UserRepresentation> results = getUsersResource(realmId).search(null, 0, Integer.MAX_VALUE);
            return results.stream().map(DefaultKeycloakFacade::toUserInfo).collect(Collectors.toList());
        }

        private UsersResource getUsersResource(String realmId) {
            return keycloak.realm(realmId).users();
        }

        @Override
        public void forEachFoundUser(String realmId, String search, int batchSize, Consumer<UserInfo> consumer) {

            int currentIndex = 0;
            while (true) {

                List<UserRepresentation> results = getUsersResource(realmId).search(search, currentIndex,
                        batchSize);
                if (results.isEmpty()) {
                    break;
                }

                results.stream().map(DefaultKeycloakFacade::toUserInfo).forEach(consumer);

                if (results.size() < batchSize) {
                    break;
                }

                currentIndex += batchSize;
            }
        }

        private static UserInfo toUserInfo(UserRepresentation userRep) {

            UserInfo userInfo = new UserInfo(userRep.getId());
            userInfo.setUsername(userRep.getUsername());
            userInfo.setFirstname(userRep.getFirstName());
            userInfo.setLastname(userRep.getLastName());
            userInfo.setEmailAddress(userRep.getEmail());
            userInfo.setAttributes(userRep.getAttributes());
            return userInfo;
        }

        @Override
        public UserReference createUser(String realmId, UserInfo userInfo) {

            UserRepresentation ur = new UserRepresentation();
            ur.setEnabled(true);
            ur.setUsername(userInfo.getUsername());
            ur.setFirstName(userInfo.getFirstname());
            ur.setLastName(userInfo.getLastname());
            ur.setEmail(userInfo.getEmailAddress());

            CredentialRepresentation password = new CredentialRepresentation();
            password.setValue("password");
            password.setType(CredentialRepresentation.PASSWORD);
            ur.setCredentials(Collections.singletonList(password));

            try (ClosableResponseWrapper wrapper = new ClosableResponseWrapper(
                    getUsersResource(realmId).create(ur))) {
                if (Response.Status.fromStatusCode(wrapper.getResponse().getStatus()) == Response.Status.CREATED) {
                    return new UserReference(wrapper.getResponse().getLocation());
                }
                return null;
            }
        }

        @Override
        public void deleteUser(String realmId, UserReference userReference) {
            try (ClosableResponseWrapper wrapper = new ClosableResponseWrapper(
                    getUsersResource(realmId).delete(userReference.getUserId()))) {
                if (Response.Status.fromStatusCode(wrapper.getResponse().getStatus()) == Response.Status.NO_CONTENT) {
                    return;
                }
                throw new RuntimeException("DELETE_USER_FAILED");
            }
        }

        class ClosableResponseWrapper implements AutoCloseable {

            private final Response response;

            public ClosableResponseWrapper(Response response) {
                this.response = response;
            }

            public Response getResponse() {
                return response;
            }

            public void close() {
                response.close();
            }
        }

        public static KeycloakClientFacadeBuilder builder() {
            return new KeycloakClientFacadeBuilder();
        }

        static class KeycloakClientFacadeBuilder {

            private String serverUrl;

            private String realmId;

            private String clientId;

            private String clientSecret;

            private String username;

            private String password;

            private ResteasyClient resteasyClient;

            public KeycloakFacade build() {

                KeycloakBuilder builder = username == null ? newKeycloakFromClientCredentials()
                        : newKeycloakFromPasswordCredentials(username, password);

                if (resteasyClient != null) {
                    builder = builder.resteasyClient(resteasyClient);
                }

                return new DefaultKeycloakFacade(builder.build());
            }

            private KeycloakBuilder newKeycloakFromClientCredentials() {
                return KeycloakBuilder.builder() //
                        .realm(realmId) //
                        .serverUrl(serverUrl)//
                        .clientId(clientId) //
                        .clientSecret(clientSecret) //
                        .grantType(OAuth2Constants.CLIENT_CREDENTIALS);
            }

            private KeycloakBuilder newKeycloakFromPasswordCredentials(String username, String password) {
                return newKeycloakFromClientCredentials() //
                        .username(username) //
                        .password(password) //
                        .grantType(OAuth2Constants.PASSWORD);
            }

            public KeycloakClientFacadeBuilder setServerUrl(String serverUrl) {
                this.serverUrl = serverUrl;
                return this;
            }

            public KeycloakClientFacadeBuilder setRealmId(String realmId) {
                this.realmId = realmId;
                return this;
            }

            public KeycloakClientFacadeBuilder setClientId(String clientId) {
                this.clientId = clientId;
                return this;
            }

            public KeycloakClientFacadeBuilder setClientSecret(String clientSecret) {
                this.clientSecret = clientSecret;
                return this;
            }

            public KeycloakClientFacadeBuilder setUsername(String username) {
                this.username = username;
                return this;
            }

            public KeycloakClientFacadeBuilder setPassword(String password) {
                this.password = password;
                return this;
            }

            public KeycloakClientFacadeBuilder setResteasyClient(ResteasyClient resteasyClient) {
                this.resteasyClient = resteasyClient;
                return this;
            }
        }
    }


    @Value
    @NonFinal
    static class UserReference {

        protected final static String UNKNOWN_ID = "00000000-0000-0000-0000-000000000000";

        private final String userId;

        public UserReference(@NonNull URI loc) {
            this(extractUserId(loc));
        }

        public UserReference(@NonNull String userId) {
            this.userId = userId;
        }

        private static String extractUserId(URI uri) {
            String path = uri.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        }
    }


    @Data
    static class UserInfo extends UserReference {

        private String username;

        private String password;

        private String emailAddress;

        private String firstname;

        private String lastname;

        private Map<String, List<String>> attributes = Collections.emptyMap();

        public UserInfo(String userId) {
            super(userId);
        }

        public UserInfo() {
            this(UserReference.UNKNOWN_ID);
        }
    }
}
