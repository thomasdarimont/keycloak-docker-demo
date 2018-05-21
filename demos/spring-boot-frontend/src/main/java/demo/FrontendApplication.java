package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

@EnableFeignClients
@SpringBootApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class FrontendApplication {

  public static void main(String[] args) {
    SpringApplication.run(FrontendApplication.class, args);
  }
}

