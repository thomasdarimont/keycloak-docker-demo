package demo.todo;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(url = "${todo-backend.server}", configuration = TodoClientConfig.class, name = "apps.tdlabs.local")
public interface TodoClient {

  @RequestMapping(method = RequestMethod.GET, value = "/todos/search/my-todos", consumes = "application/json")
  Resources<Resource<Todo>> fetchTodos();
}
