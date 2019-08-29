package demo.todo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Todo {

  final Long id;

  final String name;

  final boolean completed;
}
