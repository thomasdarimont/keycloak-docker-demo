package demo.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/simple")
class SimpleApiController {

    @GetMapping("/hello")
    String hello(@AuthenticationPrincipal Principal auth) {
        return String.format("Hi %s!", auth.getName());
    }

    @GetMapping("/claims")
    Object getClaims(@AuthenticationPrincipal JwtAuthenticationToken auth) {
        return auth.getToken().getClaims();
    }

    @GetMapping("/email")
    String getUserEmail(@AuthenticationPrincipal JwtAuthenticationToken auth) {
        System.out.printf("Token: %s%n", auth.getToken().getTokenValue());
        return auth.getToken().getClaimAsString("email");
    }
}