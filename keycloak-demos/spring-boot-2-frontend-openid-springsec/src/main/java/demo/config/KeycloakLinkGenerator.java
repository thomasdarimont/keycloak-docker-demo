package demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequestScope
@RequiredArgsConstructor
public class KeycloakLinkGenerator {

    private static final String CLIENT_LINK_TEMPLATE = "%s/realms/%s/clients/%s/redirect";

    public String createAccountLinkWithBacklink(String backlinkUri) {

        String issuer = "http://localhost";
        UriComponentsBuilder accountUri = UriComponentsBuilder
                .fromHttpUrl(issuer).path("/account");
//		UriComponentsBuilder accountUri = UriComponentsBuilder
//				.fromHttpUrl(keycloakSecurityContext.getToken().getIssuer()).path("/account")
//				.queryParam("referrer", keycloakProperties.getResource()).queryParam("referrer_uri", backlinkUri);

        return accountUri.toUriString();
    }
}
