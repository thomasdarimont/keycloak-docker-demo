package demo.todo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RepositoryRestResource(path = "todos")
interface TodoRepository extends CrudRepository<Todo, Long> {

  @PreAuthorize("hasPermission('own','todos', 'read')")
  @RestResource(path = "my-todos")
  @Query("select t from Todo t where t.owner = ?#{#security.authentication.name}")
  List<Todo> findMyTodos();
}
