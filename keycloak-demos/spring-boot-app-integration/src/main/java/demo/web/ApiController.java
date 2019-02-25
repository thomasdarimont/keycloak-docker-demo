package demo.web;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import demo.oauth2.Oauth2Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
class ApiController {

	private final Oauth2Client oauth2Client;

	@GetMapping("/api")
	Object callApi(Principal principal) {

		log.info("create oauth template...");

		RestTemplate rt = oauth2Client.createOauth2RestTemplate(principal.getName());

		log.info("make api call...");

		ResponseEntity<?> response = rt.getForEntity("http://apps.tdlabs.local:20000/todos/search/my-todos", Map.class);
		log.info("got api response.");

		return response.getBody();
	}
}
