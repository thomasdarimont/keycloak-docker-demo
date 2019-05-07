package demo.keycloak;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class KeycloakJwtAuthentication implements Authentication {

	private static final long serialVersionUID = 1L;

	private final KeycloakToken decodedToken;

	private final String tokenString;

	private final String username;

	private final Collection<? extends GrantedAuthority> authorities;

	private boolean authenticated;

	public KeycloakJwtAuthentication(KeycloakToken decodedToken, String tokenString, String username,
			Collection<? extends GrantedAuthority> authorities, boolean authenticated) {
		this.decodedToken = decodedToken;
		this.tokenString = tokenString;
		this.username = username;
		this.authorities = authorities;
		this.authenticated = authenticated;
	}

	@Override
	public String getName() {
		return username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getTokenString() {
		return tokenString;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return decodedToken;
	}

	@Override
	public Object getPrincipal() {
		return getName();
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

		if (isAuthenticated) {
			throw new IllegalArgumentException("Cannot mark this as authenticated!");
		}

		this.authenticated = false;
	}

}
