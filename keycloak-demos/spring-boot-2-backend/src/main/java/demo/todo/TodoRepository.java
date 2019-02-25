package demo.todo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "todos")
interface TodoRepository extends CrudRepository<Todo, Long> {

  @RestResource(path = "my-todos")
  @Query("select t from Todo t where t.owner = ?#{ #keycloak.token.preferredUsername }")
  List<Todo> findMyTodos();
}
