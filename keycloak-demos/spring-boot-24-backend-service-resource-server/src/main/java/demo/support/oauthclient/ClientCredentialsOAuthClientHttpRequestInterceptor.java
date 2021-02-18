package demo.support.oauthclient;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.io.IOException;
import java.util.Collections;

/**
 * {@link ClientHttpRequestInterceptor} which dynamically obtains AccessTokens with client_credentials grant
 * for the current service if necessary before executing the supplied HTTP request.
 */
@RequiredArgsConstructor
public class ClientCredentialsOAuthClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientManager oauth2Client;

    private final ClientRegistration clientRegistration;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        String accesTokenString = obtainAccessTokenForClientRegistration(oauth2Client, clientRegistration);
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accesTokenString);

        return execution.execute(request, body);
    }

    private String obtainAccessTokenForClientRegistration(OAuth2AuthorizedClientManager oauth2Client, ClientRegistration clientRegistration) {

        OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientRegistration.getRegistrationId())
                .principal(new UsernamePasswordAuthenticationToken(clientRegistration.getClientId(), Collections.emptyList()))
                .build();

        OAuth2AuthorizedClient authorizedClient = oauth2Client.authorize(oAuth2AuthorizeRequest);
        if (authorizedClient == null) {
            return "NO_TOKEN_PRESENT";
        }

        return authorizedClient.getAccessToken().getTokenValue();
    }

}