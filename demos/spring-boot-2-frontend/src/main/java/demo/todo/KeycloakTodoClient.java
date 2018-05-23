package demo.todo;

import java.util.Collections;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
class KeycloakTodoClient implements TodoClient {

	private final KeycloakRestTemplate keycloakRestTemplate;

	private final String backendUri;

	public KeycloakTodoClient(KeycloakRestTemplate keycloakRestTemplate,
			@Value("${todo-backend.server}") String backendUri) {
		this.keycloakRestTemplate = keycloakRestTemplate;
		this.backendUri = backendUri;
	}

	@Override
	public Resources<Resource<Todo>> fetchTodos() {

		ResponseEntity<Resources> response = keycloakRestTemplate.getForEntity( //
				backendUri + "/todos/search/my-todos", //
				Resources.class, //
				Collections.emptyMap() //
		);

		Resources todos = response.getBody();
		return todos;
	}

}
