package demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;

@Configuration
@EnableWebSecurity
@EnableResourceServer
@RequiredArgsConstructor
@EnableConfigurationProperties(AcmeSecurityProperties.class)
class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final ResourceServerProperties resourceServerProperties;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(resourceServerProperties.getResourceId());
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {

        http //
                .anonymous().disable() //
                .httpBasic().disable() //
                .cors().disable() // for the sake of the demo
                .headers().frameOptions().disable() //
                .and().csrf().disable() // for the sake of the demo
                .authorizeRequests().antMatchers("/api/**").authenticated();

    }

    @Bean
    public GrantedAuthoritiesMapper keycloakAuthoritiesMapper() {

        SimpleAuthorityMapper mapper = new SimpleAuthorityMapper();
        mapper.setConvertToUpperCase(true);
        return mapper;
    }

    /**
     * Retrieves Public-Keys from JWKs end-point, in order to support key rotation.
     *
     * @param jwtTokenEnhancer
     * @return
     */
    @Bean
    public TokenStore jwkTokenStore(DefaultAccessTokenConverter jwtTokenEnhancer) {
        return new JwkTokenStore(this.resourceServerProperties.getJwk().getKeySetUri(), jwtTokenEnhancer);
    }
}
