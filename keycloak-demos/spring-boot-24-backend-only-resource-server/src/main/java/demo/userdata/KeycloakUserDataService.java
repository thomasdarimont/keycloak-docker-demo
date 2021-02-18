package demo.userdata;

/**
 * Service to retrieve Keycloak user data
 */
public interface KeycloakUserDataService {

    KeycloakUserData getDataForCurrentUser();

    KeycloakUserData getDataForCustomer(String customerId);
}
