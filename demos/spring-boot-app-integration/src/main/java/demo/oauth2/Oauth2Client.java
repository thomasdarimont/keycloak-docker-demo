package demo.oauth2;

import java.net.URL;
import java.security.interfaces.RSAKey;
import java.util.function.Consumer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwk.GuavaCachedJwkProvider;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import demo.oauth2.TokensStore.Tokens;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Oauth2Client {

	private final TokensStore tokenStore;

	private final Oauth2Properties oauthProperties;

	private final RestTemplate oauthRestTemplate;

	private final JwkProvider jwkProvider;

	public Oauth2Client(TokensStore tokenStore, Oauth2Properties oauthProperties) throws Exception {
		this.tokenStore = tokenStore;
		this.oauthProperties = oauthProperties;

		RestTemplate rt = new RestTemplate();
		rt.getInterceptors().add(createClientAuthInterceptor(oauthProperties));

		this.oauthRestTemplate = rt;
		this.jwkProvider = new GuavaCachedJwkProvider(
				traceJwkLookupsOf(new UrlJwkProvider(new URL(oauthProperties.getJwksEndpoint()))));
	}

	private BasicAuthorizationInterceptor createClientAuthInterceptor(Oauth2Properties oauthProperties) {
		return new BasicAuthorizationInterceptor(oauthProperties.getClientId(), oauthProperties.getClientSecret());
	}

	public RestTemplate createOauth2RestTemplate(String username) {

		RestTemplate rt = new RestTemplate();
		rt.getInterceptors().add((request, body, execution) -> {

			Tokens tokens = reuseOrObtainAccessToken(username);
			request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken());

			return execution.execute(request, body);
		});

		return rt;
	}

	private Tokens reuseOrObtainAccessToken(String username) {

		Tokens currentTokens = tokenStore.getTokens(username);

		if (currentTokens == null) {
			throw new AccessDeniedException("Bad token!");
		}

		if (currentTokens.isAccessTokenStillValid()) {
			log.info("reuse access token...");
			return currentTokens;
		}

		log.info("obtain new access token");

		Tokens newTokens = requestNewTokensWithRefreshToken(currentTokens);

		return tokenStore.storeTokens(username, newTokens);
	}

	public Tokens requestNewTokensWithCode(String username, String code, String sessionState) {

		log.info("obtain initial tokens...");

		HttpEntity<MultiValueMap<String, String>> request = newFormRequest(params -> {
			params.add("code", code);
			params.add("session_state", sessionState);
			params.add("grant_type", "authorization_code");
			params.add("scope", "profile");
			params.add("redirect_uri", oauthProperties.getRedirectUri());
			params.add("client_id", oauthProperties.getClientId());
		});

		Tokens tokens = requestNewTokens(request);

		log.info("obtained initial tokens.");

		return tokenStore.storeTokens(username, tokens);
	}

	private Tokens requestNewTokensWithRefreshToken(Tokens tokens) {

		HttpEntity<MultiValueMap<String, String>> request = newFormRequest(params -> {
			params.add("refresh_token", tokens.getRefreshToken());
			params.add("grant_type", "refresh_token");
		});

		return requestNewTokens(request);
	}

	@SuppressWarnings("unchecked")
	private <Payload extends MultiValueMap<String, String>> HttpEntity<Payload> newFormRequest(
			Consumer<Payload> enricher) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		Payload parameters = (Payload) new LinkedMultiValueMap<String, String>();
		enricher.accept(parameters);

		return new HttpEntity<Payload>(parameters, headers);
	}

	private Tokens requestNewTokens(HttpEntity<MultiValueMap<String, String>> request) {

		try {
			ResponseEntity<Tokens> response = oauthRestTemplate.postForEntity(oauthProperties.getTokenEndpoint(),
					request, Tokens.class);
			Tokens newTokens = response.getBody();

			JWT decoded = JWT.decode(newTokens.getAccessToken());
			newTokens.addAdditional("subject", decoded.getSubject());

			verifyJwt(decoded);

			return newTokens;
		} catch (HttpClientErrorException hcee) {
			if (hcee.getRawStatusCode() == 400) {
				throw new AccessDeniedException("Bad token!", hcee);
			}
		}

		return null;
	}

	private void verifyJwt(JWT decoded) {

		try {
			Jwk jwk = jwkProvider.get(decoded.getKeyId());

			// TODO check for Algorithm
			JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAKey) jwk.getPublicKey())).build();
			verifier.verify(decoded.getToken());

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Bad token!");
		}
	}

	private JwkProvider traceJwkLookupsOf(JwkProvider provider) {

		return (String keyId) -> {
			try {
				log.info("Begin lookup for JWK with Kid={}", keyId);
				return provider.get(keyId);
			} finally {
				log.info("End lookup for JWK with Kid={}", keyId);
			}
		};
	}
}
