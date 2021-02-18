package demo.support.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Allows user code to extract the current Jwt
 */
@Component
public class CurrentAccessTokenProvider {

    public Jwt currentAccessToken() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return (Jwt) auth.getCredentials();
    }
}
