package demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
class PingController {

    @GetMapping("/ping")
    public Object ping() {
        return Collections.singletonMap("status", "ok");
    }
}
