package demo.web;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import demo.oauth2.Oauth2Client;
import demo.oauth2.Oauth2Properties;
import demo.oauth2.TokensStore.Tokens;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ConnectController {

	private final Oauth2Client oauth2Client;

	private final Oauth2Properties oauthProperties;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("oauth", oauthProperties);
		return "index";
	}

	@GetMapping(path = "/connect", params = { "session_state", "code" })
	public String connect(@Valid ConnectRequest connectRequest, Principal principal) {

		log.info("Obtain tokens...");

		Tokens tokens = oauth2Client.requestNewTokensWithCode( //
				principal.getName(), connectRequest.getCode(), connectRequest.getSession_state());

		log.info("Obtained tokens for user: {} with external_user_id: {}", principal.getName(),
				tokens.getAdditionalValues().get("subject"));
		return "redirect:/connected";
	}

	@GetMapping(path = "/connected")
	public String connect(Model model) {
		return "connected";
	}

	@Data
	static class ConnectRequest {

		@NotEmpty
		String session_state;

		@NotEmpty
		String code;

		Map<String, Object> additionalValues = new HashMap<>();

		@JsonAnySetter
		public void addAdditional(String key, Object value) {
			additionalValues.put(key, value);
		}
	}
}
