package demo.todo;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
class KeycloakTodoClient implements TodoClient {

    private final KeycloakRestTemplate keycloakRestTemplate;

    private final String backendUri;

    public KeycloakTodoClient( //
                               KeycloakRestTemplate keycloakRestTemplate, //
                               @Value("${todo-backend.server}") String backendUri //
    ) {
        this.keycloakRestTemplate = keycloakRestTemplate;
        this.backendUri = backendUri;
    }

    @Override
    public CollectionModel<EntityModel<Todo>> fetchTodos() {

        ResponseEntity<CollectionModel> response = keycloakRestTemplate.getForEntity( //
                backendUri + "/todos/search/my-todos", //
                CollectionModel.class, //
                Collections.emptyMap() //
        );

        CollectionModel todos = response.getBody();
        return todos;
    }

}
