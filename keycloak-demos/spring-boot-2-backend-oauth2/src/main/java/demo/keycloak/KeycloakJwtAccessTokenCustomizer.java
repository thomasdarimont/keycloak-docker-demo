package demo.keycloak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import demo.config.AcmeSecurityProperties;
import demo.config.AcmeSecurityProperties.Jwt;
import lombok.RequiredArgsConstructor;

/**
 * {@link KeycloakJwtAccessTokenCustomizer} reads roles and user-info from the
 * given access token.
 */
@Component
@RequiredArgsConstructor
class KeycloakJwtAccessTokenCustomizer extends DefaultAccessTokenConverter
        implements JwtAccessTokenConverterConfigurer {

    private final ResourceServerProperties resourceServerProperties;

    private final AcmeSecurityProperties restSecurityProperties;

    private final ObjectMapper objectMapper;

    private final GrantedAuthoritiesMapper authoritiesMapper;

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> tokenMap) {

        OAuth2Authentication authentication = super.extractAuthentication(tokenMap);

        KeycloakToken keycloakToken = objectMapper.convertValue(tokenMap, KeycloakToken.class);

        Collection<? extends GrantedAuthority> authorities = mapKeycloakRolesToAuthorities( //
                keycloakToken.getRealmRoles(), //
                keycloakToken.getClientRoles(resourceServerProperties.getResourceId()) //
        );

        KeycloakJwtAuthentication keycloakAuth = new KeycloakJwtAuthentication( //
                keycloakToken, //
                getCurrentJwtToken(), //
                extractUsername(keycloakToken, authentication), //
                authorities, authentication.isAuthenticated() //
        );

        OAuth2Request request = mapOauth2Request(authentication, keycloakToken, authorities);

        return new OAuth2Authentication(request, keycloakAuth);
    }

    private String getCurrentJwtToken() {
        return getCurrentHttpRequest().map(this::tokenFromRequest).orElse("missing_jwt");
    }

    private OAuth2Request mapOauth2Request( //
                                            OAuth2Authentication authentication, //
                                            KeycloakToken keycloakToken, //
                                            Collection<? extends GrantedAuthority> authorities //
    ) {

        OAuth2Request oAuth2Request = authentication.getOAuth2Request();

        return new OAuth2Request(oAuth2Request.getRequestParameters(), oAuth2Request.getClientId(), authorities, true,
                oAuth2Request.getScope(), keycloakToken.getAudience(), null, null, null);
    }

    public static Optional<HttpServletRequest> getCurrentHttpRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes()) //
                .filter(requestAttributes -> ServletRequestAttributes.class
                        .isAssignableFrom(requestAttributes.getClass())) //
                .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes)) //
                .map(ServletRequestAttributes::getRequest);
    }

    private String tokenFromRequest(HttpServletRequest request) {

        String value = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (value == null || !value.toLowerCase().startsWith("bearer")) {
            return null;
        }

        String[] parts = value.split(" ");
        if (parts.length < 2) {
            return null;
        }

        return parts[1].trim();
    }

    private Collection<? extends GrantedAuthority> mapKeycloakRolesToAuthorities(Set<String> realmRoles,
                                                                                 Set<String> clientRoles) {

        List<GrantedAuthority> combinedAuthorities = new ArrayList<>();

        combinedAuthorities.addAll(authoritiesMapper.mapAuthorities(realmRoles.stream() //
                .map(SimpleGrantedAuthority::new) //
                .collect(Collectors.toList())));

        combinedAuthorities.addAll(authoritiesMapper.mapAuthorities(clientRoles.stream() //
                .map(SimpleGrantedAuthority::new) //
                .collect(Collectors.toList())));

        return combinedAuthorities;
    }

    private String extractUsername(KeycloakToken token, OAuth2Authentication authentication) {

        Jwt jwt = restSecurityProperties.getJwt();

        if (jwt.getUsernameField() == null) {
            return String.valueOf(authentication.getPrincipal());
        }

        switch (jwt.getUsernameField()) {
            case SUB:
                return token.getSub();
            case PREFERRED_USERNAME:
                return token.getPreferredUsername();
            case CLAIM:
                return String.valueOf(token.getAdditionalClaims().get(jwt.getUsernameClaim()));
            default:
                return String.valueOf(authentication.getPrincipal());
        }
    }
}
