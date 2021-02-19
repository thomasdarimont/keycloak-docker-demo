package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Entry point into the application, sets up initial scan for
 * Spring beans
 */
@SpringBootApplication
public class UserDataServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(UserDataServiceApp.class, args);
    }

    @Autowired
    public void onStartup(@Qualifier("serviceClientRestTemplate") RestTemplate serviceAccountRestTemplate, Environment environment) {

        System.out.println("Fetching user data with service account");
        String userInfoUri = environment.getProperty("springkeycloak.auth.jwt.issuerUri") + "/protocol/openid-connect/userinfo";
        System.out.println(serviceAccountRestTemplate.getForObject(userInfoUri, Map.class));
    }
}
