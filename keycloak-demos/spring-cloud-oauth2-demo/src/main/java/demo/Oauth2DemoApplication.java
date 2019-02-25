package demo;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@EnableOAuth2Sso
@SpringBootApplication
public class Oauth2DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2DemoApplication.class, args);
	}
}

@RestController
@RequiredArgsConstructor
class DemoController {

	@Value("${demo.keycloak.accountUrl}")
	String accountUrl;

	@Value("${demo.keycloak.externalBaseUrl}")
	String externalBaseUrl;

	@Value("${security.oauth2.client.clientId}")
	String clientId;

	@GetMapping("/")
	String home(Principal user) {
		return "Hello " + user.getName();
	}

	@GetMapping("/account")
	void account(HttpServletResponse response, Principal user) throws Exception {

		String url = String.format("%s?referrer=%s&referrer_uri=%s", accountUrl, clientId, externalBaseUrl);
		response.sendRedirect(url);
	}
}