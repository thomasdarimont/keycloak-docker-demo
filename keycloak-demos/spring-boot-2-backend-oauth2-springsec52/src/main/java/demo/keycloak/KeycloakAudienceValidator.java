package demo.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
class KeycloakAudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final OAuth2Error ERROR_INVALID_AUDIENCE = new OAuth2Error("invalid_token", "Invalid audience", null);

    @Value("${keycloak.clientId}")
    String allowedAudience;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {


        if (!jwt.getAudience().contains(allowedAudience)) {
            return OAuth2TokenValidatorResult.failure(ERROR_INVALID_AUDIENCE);
        }

        return OAuth2TokenValidatorResult.success();
    }
}
