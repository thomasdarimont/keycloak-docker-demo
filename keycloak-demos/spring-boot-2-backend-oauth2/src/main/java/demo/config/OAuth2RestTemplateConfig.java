package demo.config;

import demo.config.OAuth2RestTemplateConfig.IfServiceAccountEnabled;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

/**
 * OAuth2RestTemplateConfig provides a {@link OAuth2RestTemplate} that can act on behalf of this application.
 * Note this configuration is only activated if the {@code security.oauth2.client.grant-type=client_credentials} configuration property is set.
 */
@Configuration
@Conditional(IfServiceAccountEnabled.class)
class OAuth2RestTemplateConfig {

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails details) {

        OAuth2RestTemplate rt = new OAuth2RestTemplate(details);
        rt.getAccessToken();
        return rt;
    }

    static class IfServiceAccountEnabled extends AllNestedConditions {

        IfServiceAccountEnabled() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(prefix = "security.oauth2.client", value = "grant-type", havingValue = "client_credentials")
        static class Oauth2ClientCredentialsGrantConfigured {
        }

    }
}
