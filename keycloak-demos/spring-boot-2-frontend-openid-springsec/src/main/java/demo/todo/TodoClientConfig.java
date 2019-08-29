package demo.todo;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

class TodoClientConfig {

	/**
	 * {@link RequestInterceptor} that adds the Keycloak Access-Token to the
	 * Authorization Header
	 */
	@Bean
	protected RequestInterceptor keycloakRequestInterceptor() {
		return new KeycloakRequestInterceptor();
	}

	@RequiredArgsConstructor
	static class KeycloakRequestInterceptor implements RequestInterceptor {

		@Override
		public void apply(RequestTemplate template) {

			ensureTokenIsStillValid();

			// We use the Access-Token of the current user to call the service
			// Authorization: Bearer
			// eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJMT0Rx....
			String accessToken = ""; // keycloakSecurityContext.getTokenString();
			template.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		}

		private void ensureTokenIsStillValid() {
//			if (keycloakSecurityContext instanceof RefreshableKeycloakSecurityContext) {
//				RefreshableKeycloakSecurityContext.class.cast(keycloakSecurityContext).refreshExpiredToken(true);
//			}
		}
	}
}
