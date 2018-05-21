package demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import demo.app.HelloworldView;

@SpringBootApplication
public class KeycloakJavafxClientDemoApp extends AbstractJavaFxApplicationSupport {

	public static void main(String[] args) {
		launchApp(KeycloakJavafxClientDemoApp.class, HelloworldView.class, args);
	}
}
