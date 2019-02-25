package demo.web;

import java.util.Collections;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PingController {

	@GetMapping("/ping")
	public Object ping() {
		return Collections.singletonMap("status", "ok");
	}
}
