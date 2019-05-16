package demo.api;

import demo.keycloak.KeycloakJwtAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simple")
class SimpleApiController {

    @GetMapping("/hello")
    String hello() {
        return "Hi there!";
    }

    @GetMapping("/claims")
    Object getClaims(@AuthenticationPrincipal KeycloakJwtAuthenticationToken auth) {
        return auth.getToken().getClaims();
    }

    @GetMapping("/email")
    String getUserEmail(@AuthenticationPrincipal KeycloakJwtAuthenticationToken auth) {
        return "Subscriber: " + auth.getToken().getClaimAsString("email");
    }
}