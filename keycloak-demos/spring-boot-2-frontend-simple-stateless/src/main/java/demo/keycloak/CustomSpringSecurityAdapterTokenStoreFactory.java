package demo.keycloak;

import org.keycloak.adapters.AdapterTokenStore;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.springsecurity.token.SpringSecurityAdapterTokenStoreFactory;
import org.keycloak.enums.TokenStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomSpringSecurityAdapterTokenStoreFactory extends SpringSecurityAdapterTokenStoreFactory {

    @Override
    public AdapterTokenStore createAdapterTokenStore(KeycloakDeployment deployment, HttpServletRequest request, HttpServletResponse response) {

        if (deployment.getTokenStore() == TokenStore.COOKIE) {
            return new CustomSpringSecurityCookieTokenStore(deployment, request, response);
        }

        return super.createAdapterTokenStore(deployment, request, response);
    }
}
