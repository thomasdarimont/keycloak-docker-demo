package demo.userdata;

/**
 * Service to retrieve user data
 */
public interface CustomUserDataService {

    CustomUserData getDataForCurrentUser();

    CustomUserData getDataForCustomer(String customerId);
}
