package demo.todo;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

public class TodoClientConfig {

	@Bean
	protected RequestInterceptor keycloakSecurityContextRequestInterceptor(
			KeycloakSecurityContext keycloakSecurityContext) {
		return new KeycloakSecurityContextRequestInterceptor(keycloakSecurityContext);
	}

	@RequiredArgsConstructor
	static class KeycloakSecurityContextRequestInterceptor implements RequestInterceptor {

		private final KeycloakSecurityContext keycloakSecurityContext;

		@Override
		public void apply(RequestTemplate template) {

			if (keycloakSecurityContext instanceof RefreshableKeycloakSecurityContext) {
				RefreshableKeycloakSecurityContext.class.cast(keycloakSecurityContext).refreshExpiredToken(true);
			}

			template.header(HttpHeaders.AUTHORIZATION, "Bearer " + keycloakSecurityContext.getTokenString());
		}
	}
}
