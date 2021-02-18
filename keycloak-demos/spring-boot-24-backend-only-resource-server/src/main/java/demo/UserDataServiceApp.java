package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point into the application, sets up initial scan for
 * Spring beans
 */
@SpringBootApplication
public class UserDataServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(UserDataServiceApp.class, args);
    }

}
