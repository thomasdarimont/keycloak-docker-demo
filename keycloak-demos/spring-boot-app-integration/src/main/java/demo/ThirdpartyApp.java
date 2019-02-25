package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import demo.oauth2.Oauth2Properties;

@SpringBootApplication
@EnableConfigurationProperties(Oauth2Properties.class)
public class ThirdpartyApp {

	public static void main(String[] args) {
		SpringApplication.run(ThirdpartyApp.class, args);
	}
}
