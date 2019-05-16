package demo.keycloak;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

public class KeycloakJwtAuthenticationToken extends JwtAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final String username;

    public KeycloakJwtAuthenticationToken(Jwt jwt, String username, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public Object getPrincipal() {
        return getName();
    }
}
