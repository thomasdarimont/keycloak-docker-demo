package demo.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Enables the {@link KeycloakDataServiceProperties}
 */

@Configuration
@EnableConfigurationProperties(KeycloakDataServiceProperties.class)
public class KeycloakDataServiceConfig {
}
