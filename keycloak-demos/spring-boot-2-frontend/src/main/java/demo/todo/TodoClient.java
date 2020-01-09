package demo.todo;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

public interface TodoClient {

    CollectionModel<EntityModel<Todo>> fetchTodos();
}
