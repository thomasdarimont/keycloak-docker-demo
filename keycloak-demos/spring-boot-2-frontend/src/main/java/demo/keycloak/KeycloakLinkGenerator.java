package demo.keycloak;

import lombok.RequiredArgsConstructor;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequestScope
@RequiredArgsConstructor
public class KeycloakLinkGenerator {

    private static final String CLIENT_LINK_TEMPLATE = "%s/realms/%s/clients/%s/redirect";

    private final KeycloakSpringBootProperties keycloakProperties;

    private final KeycloakSecurityContext keycloakSecurityContext;

    /**
     * Generates a link, that redirects to the baseUrl of the given application
     * <p>
     * See {@code fragments.html}
     *
     * @param clientId
     * @return
     */
    public String getClientLink(String clientId) {
        return String.format(CLIENT_LINK_TEMPLATE, //
                keycloakProperties.getAuthServerUrl(), //
                keycloakProperties.getRealm(), //
                clientId //
        );
    }

    public String createAccountLinkWithBacklink(String backlinkUri) {

        UriComponentsBuilder accountUri = UriComponentsBuilder
                .fromHttpUrl(keycloakSecurityContext.getToken().getIssuer()).path("/account")
                .queryParam("referrer", keycloakProperties.getResource()).queryParam("referrer_uri", backlinkUri);

        return accountUri.toUriString();
    }
}
