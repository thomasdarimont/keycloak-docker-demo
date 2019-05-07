package demo.api;

import demo.keycloak.KeycloakJwtAuthentication;
import demo.keycloak.KeycloakToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Instant;

@RestController
@RequestMapping("/api/greetings")
@RequiredArgsConstructor
class GreetingApiController {

//    private final OAuth2RestOperations oauth2RestTemplate;

    @GetMapping("user")
    Object greet(Principal user) {


        return String.format("Hello %s %s", user.getName(), Instant.now());
    }

    @GetMapping("token")
    Object token(OAuth2Authentication auth) {

        KeycloakJwtAuthentication jwtAuth = (KeycloakJwtAuthentication) auth.getUserAuthentication();
        KeycloakToken details = (KeycloakToken) jwtAuth.getDetails();

        return details;
    }
}