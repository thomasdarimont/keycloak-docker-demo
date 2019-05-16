package demo.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final GrantedAuthoritiesMapper authoritiesMapper;

    private final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

    @Override
    public KeycloakJwtAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities = mapKeycloakRolesToAuthorities( //
                getRealmRolesFrom(jwt), //
                getClientRolesFrom(jwt, "app-backend-springboot-oauth2") //
        );

        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) jwtAuthenticationConverter.convert(jwt);
        authorities.addAll(jwtAuth.getAuthorities());

        return new KeycloakJwtAuthenticationToken( //
                jwt, //
                getUsernameFrom(jwt), //
                authorities
        );
    }

    protected String getUsernameFrom(Jwt jwt) {

        if (jwt.containsClaim("preferred_username")) {
            return jwt.getClaimAsString("preferred_username");
        }

        return jwt.getSubject();
    }


    protected Collection<GrantedAuthority> mapKeycloakRolesToAuthorities(Set<String> realmRoles,
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

    protected Set<String> getRealmRolesFrom(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

        if (CollectionUtils.isEmpty(realmAccess)) {
            return Collections.emptySet();
        }

        Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
        if (CollectionUtils.isEmpty(realmRoles)) {
            return Collections.emptySet();
        }

        return realmRoles.stream().map(this::normalizeRole).collect(Collectors.toSet());
    }

    protected Set<String> getClientRolesFrom(Jwt jwt, String clientId) {

        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

        if (CollectionUtils.isEmpty(resourceAccess)) {
            return Collections.emptySet();
        }

        Map<String, List<String>> clientAccess = (Map<String, List<String>>) resourceAccess.get(clientId);
        if (CollectionUtils.isEmpty(clientAccess)) {
            return Collections.emptySet();
        }

        List<String> clientRoles = clientAccess.get("roles");
        if (CollectionUtils.isEmpty(clientRoles)) {
            return Collections.emptySet();
        }

        return clientRoles.stream().map(this::normalizeRole).collect(Collectors.toSet());
    }

    private String normalizeRole(String role) {
        return role.replace('-', '_');
    }
}
