package demo;


import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import demo.todos.ui.TodosView;
import org.keycloak.adapters.installed.KeycloakInstalled;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

@EnableFeignClients
@SpringBootApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class JavaFxFrontendApplication extends AbstractJavaFxApplicationSupport {

  public static final KeycloakInstalled KEYCLOAK = new KeycloakInstalled();

  public static void main(String[] args) {
    launchApp(JavaFxFrontendApplication.class, TodosView.class, args);
  }

  @Override
  public void init() throws Exception {

    KEYCLOAK.loginDesktop();

    super.init();
  }
}