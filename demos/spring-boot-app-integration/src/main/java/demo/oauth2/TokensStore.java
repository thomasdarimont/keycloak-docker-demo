package demo.oauth2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokensStore {

	private final ConcurrentMap<String, Tokens> store = new ConcurrentHashMap<>();

	public Tokens storeTokens(String key, Tokens tokens) {
		store.put(key, tokens);
		return tokens;
	}

	public Tokens getTokens(String key) {
		return store.get(key);
	}

	@Data
	public static class Tokens {

		private static final long MIN_SECONDS = 3;

		private final long createdAt = System.currentTimeMillis();

		@JsonProperty(value = "access_token")
		private String accessToken;

		@JsonProperty(value = "refresh_token")
		private String refreshToken;

		@JsonProperty(value = "refresh_expires_in")
		private long refreshExpiresInSeconds;

		@JsonProperty(value = "expires_in")
		private long expiresInSeconds;

		Map<String, Object> additionalValues = new HashMap<>();

		public Tokens() {
		}

		public Tokens(String accessToken, String refreshToken, long refreshExpiresInSeconds, long expiresInSeconds) {
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
			this.refreshExpiresInSeconds = refreshExpiresInSeconds;
			this.expiresInSeconds = expiresInSeconds;
		}

		@JsonAnySetter
		public void addAdditional(String key, Object value) {
			additionalValues.put(key, value);
		}

		public boolean isAccessTokenStillValid() {
			
			long ageInSeconds = (System.currentTimeMillis() - createdAt) / 1000;
			long maxAgeInSeconds = expiresInSeconds - MIN_SECONDS;

			boolean valid = ageInSeconds <= maxAgeInSeconds;

			if (valid) {
				TokensStore.log.info("token still valid for: {} seconds", maxAgeInSeconds - ageInSeconds);
			}

			return valid;
		}

	}
}
