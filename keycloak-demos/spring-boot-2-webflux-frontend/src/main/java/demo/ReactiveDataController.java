package demo;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Instant;
import java.util.Collections;

@RestController
@RequestMapping("/data")
class ReactiveDataController {

    @GetMapping
    private Mono<Object> getData(@AuthenticationPrincipal Principal currentUser) {
        return Mono.just(Collections.singletonMap("greeting", String.format("Hello %s %s", currentUser.getName(), Instant.now())));
    }

}