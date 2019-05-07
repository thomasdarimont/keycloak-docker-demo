package demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "acme.security")
public class AcmeSecurityProperties {

    private Jwt jwt = new Jwt();

    private OAuth2 oauth2 = new OAuth2();

    @Getter
    @Setter
    public static class OAuth2 {

        private String issuerUri;

    }

    @Getter
    @Setter
    public static class Jwt {

        private UsernameField usernameField = UsernameField.PREFERRED_USERNAME;

        private String usernameClaim;

        public enum UsernameField {
            SUB, PREFERRED_USERNAME, CLAIM
        }
    }

}
