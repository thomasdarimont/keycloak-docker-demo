package demo;


import demo.keycloak.KeycloakJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;

@EnableWebSecurity
@RequiredArgsConstructor
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/claims").hasAuthority("SCOPE_openid")
                .mvcMatchers("/email").hasAuthority("SCOPE_email")
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer().jwt().jwtAuthenticationConverter(keycloakJwtAuthenticationConverter);
    }

    @Bean
    JwtDecoder jwtDecoder(List<OAuth2TokenValidator<Jwt>> validators, OAuth2ResourceServerProperties properties) {

        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(properties.getJwt().getIssuerUri());
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));

        return jwtDecoder;
    }

    @Bean
    OAuth2TokenValidator<Jwt> defaultTokenValidator(OAuth2ResourceServerProperties properties) {
        return JwtValidators.createDefaultWithIssuer(properties.getJwt().getIssuerUri());
    }
}
