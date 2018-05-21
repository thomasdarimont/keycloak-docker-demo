package demo.todos;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import demo.JavaFxFrontendApplication;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class TodoClientConfig {

	@Bean
	protected RequestInterceptor keycloakSecurityContextRequestInterceptor() {
		return new KeycloakSecurityContextRequestInterceptor();
	}

	static class KeycloakSecurityContextRequestInterceptor implements RequestInterceptor {

		@Override
		public void apply(RequestTemplate template) {

			try {
				String accessToken = JavaFxFrontendApplication.KEYCLOAK.getTokenString(5, TimeUnit.SECONDS);
				template.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
