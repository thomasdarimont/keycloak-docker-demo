package demo.todo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
class Todo {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  private String description;

  private boolean completed;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private String ownerId;
}
