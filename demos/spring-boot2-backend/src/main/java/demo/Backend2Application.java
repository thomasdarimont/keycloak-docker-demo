package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@EntityScan(
  basePackageClasses = {Backend2Application.class, Jsr310JpaConverters.class}
)
@SpringBootApplication
public class Backend2Application {

  public static void main(String[] args) {
    SpringApplication.run(Backend2Application.class, args);
  }
}
