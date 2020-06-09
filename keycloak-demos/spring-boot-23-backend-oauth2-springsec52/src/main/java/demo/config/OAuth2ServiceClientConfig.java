package demo.config;

import demo.support.oauthclient.ClientCredentialsOAuthClientHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

/**
 * OAuth2ServiceClientConfig provides a {@link RestTemplate} that can act on behalf of this application.
 * Note this configuration is only activated if the {@code spring.security.oauth2.client.registration.self.provider=keycloak} configuration property is set.
 */
@Configuration
@Conditional(OAuth2ServiceClientConfig.IfServiceAccountEnabled.class)
class OAuth2ServiceClientConfig {

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(OAuth2AuthorizedClientService clientService,
                                                                 ClientRegistrationRepository clientRepository) {

        var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        var clientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRepository, clientService);
        clientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return clientManager;
    }

    @Bean
    @Qualifier("serviceClientRestTemplate")
    public RestTemplate serviceClientRestTemplate(OAuth2AuthorizedClientManager authorizedClientManager, ClientRegistrationRepository clientRepository) {

        ClientRegistration clientRegistration = clientRepository.findByRegistrationId("self");

        var rt = new RestTemplate();
        rt.getInterceptors().add(new ClientCredentialsOAuthClientHttpRequestInterceptor(authorizedClientManager, clientRegistration));

        return rt;
    }

    static class IfServiceAccountEnabled extends AllNestedConditions {

        IfServiceAccountEnabled() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(prefix = "spring.security.oauth2.client.registration.self", value = "provider", havingValue = "keycloak")
        static class Oauth2ClientCredentialsGrantConfigured {
        }
    }
}