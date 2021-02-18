package demo.config;

import demo.support.keycloak.KeycloakJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration applied on all web endpoints defined for this
 * application. Any configuration on specific resources is applied
 * in addition to these global rules.
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class KeycloakWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    /**
     * Configures basic security handler per HTTP session.
     * <p>
     * <ul>
     * <li>Stateless session (no session kept server-side)</li>
     * <li>CORS set up</li>
     * <li>Require the role "ACCESS" for all api paths</li>
     * <li>JWT converted into Spring token</li>
     * </ul>
     *
     * @param http security configuration
     * @throws Exception any error
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors(this::configureCors)
                .authorizeRequests()
                // declarative route configuration
                .mvcMatchers("/api").hasAuthority("ROLE_ACCESS")
                //...
                .anyRequest().fullyAuthenticated()
                .and()
                .oauth2ResourceServer()
                .jwt().jwtAuthenticationConverter(keycloakJwtAuthenticationConverter);
    }

    /**
     * Configures CORS to allow requests from localhost:30000
     *
     * @param cors mutable cors configuration
     */
    protected void configureCors(CorsConfigurer<HttpSecurity> cors) {

        UrlBasedCorsConfigurationSource defaultUrlBasedCorsConfigSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.addAllowedOrigin("http://localhost:30000");
        defaultUrlBasedCorsConfigSource.registerCorsConfiguration("/api/**", corsConfiguration);

        cors.configurationSource(req -> {

            CorsConfiguration config = new CorsConfiguration();

            config = config.combine(defaultUrlBasedCorsConfigSource.getCorsConfiguration(req));

            // check if request Header "origin" is in white-list -> dynamically generate cors config

            return config;
        });
    }
}