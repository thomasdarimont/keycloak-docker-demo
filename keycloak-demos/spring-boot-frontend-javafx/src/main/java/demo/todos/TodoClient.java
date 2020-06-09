package demo.todos;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(url = "${todo-backend.server}", configuration = TodoClientConfig.class, name = "localhost")
public interface TodoClient {

  @RequestMapping(method = RequestMethod.GET, value = "/todos/search/my-todos", consumes = "application/json")
  CollectionModel<EntityModel<Todo>> fetchTodos();
}
