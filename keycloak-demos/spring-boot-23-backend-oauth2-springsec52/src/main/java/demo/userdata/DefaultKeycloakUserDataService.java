package demo.userdata;

import demo.support.jwt.CurrentAccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class DefaultKeycloakUserDataService implements KeycloakUserDataService {

    private final CurrentAccessTokenProvider currentAccessTokenProvider;

    @Override
    @PreAuthorize("hasPermission('self', 'customer', 'access')")
    public KeycloakUserData getDataForCurrentUser() {
        var currentAccessToken = currentAccessTokenProvider.currentAccessToken();

        return getDataForCustomer(currentAccessToken.getSubject());
    }

    @Override
    @PreAuthorize("hasPermission(#customerId, 'customer', 'access')")
    public KeycloakUserData getDataForCustomer(String customerId) {

        var currentAccessToken = currentAccessTokenProvider.currentAccessToken();

        // for now we only reflect the data from the token, later we 'll fetch additional data from the actual datastore

        var data = new KeycloakUserData();
        data.setUserId(customerId);
        data.setUsername(currentAccessToken.getClaimAsString("preferred_username"));

        return data;
    }
}
