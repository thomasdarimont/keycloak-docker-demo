package demo.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("demo.oauth2")
public class Oauth2Properties {

	String clientId;

	String clientSecret;

	String tokenEndpoint;

	String authEndpoint;

	String jwksEndpoint;

	String redirectUri;
}
