package demo.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
class KeycloakAudienceValidator implements OAuth2TokenValidator<Jwt> {

    private OAuth2Error error =
            new OAuth2Error("invalid_token", "Invalid audience", null);

    @Value("${keycloak.client_id:app-backend-springboot-oauth2}")
    String allowedAudience;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {


        if (!jwt.getAudience().contains(allowedAudience)) {
            return OAuth2TokenValidatorResult.failure(error);
        }

        return OAuth2TokenValidatorResult.success();
    }
}
