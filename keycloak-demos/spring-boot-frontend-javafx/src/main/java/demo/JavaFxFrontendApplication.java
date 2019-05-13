package demo;


import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import demo.todos.ui.TodosView;
import org.keycloak.adapters.installed.KeycloakInstalled;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

@EnableFeignClients
@SpringBootApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class JavaFxFrontendApplication extends AbstractJavaFxApplicationSupport {

  public static final KeycloakInstalled KEYCLOAK = new KeycloakInstalled(){

    HttpResponseWriter loginResponseWriter;

    HttpResponseWriter logoutResponseWriter;

    @Override
    public void setLoginResponseWriter(HttpResponseWriter loginResponseWriter) {
      super.setLoginResponseWriter(loginResponseWriter);
      this.loginResponseWriter = loginResponseWriter;
    }

    @Override
    public HttpResponseWriter getLoginResponseWriter() {
      return loginResponseWriter;
    }

    @Override
    public void setLogoutResponseWriter(HttpResponseWriter logoutResponseWriter) {
      super.setLogoutResponseWriter(logoutResponseWriter);
      this.logoutResponseWriter = logoutResponseWriter;
    }

    @Override
    public HttpResponseWriter getLogoutResponseWriter() {
      return logoutResponseWriter;
    }
  };

  public static void main(String[] args) throws Exception{
    launchApp(JavaFxFrontendApplication.class, TodosView.class, args);

//    KeycloakInstalled keycloakInstalled = new KeycloakInstalled();
//
//    keycloakInstalled.logout();
//    keycloakInstalled.loginDesktop();
//
//    String accessTokenString = keycloakInstalled.getTokenString(5, TimeUnit.MINUTES);
//    System.out.println("AccessToken: " + accessTokenString);
//    System.out.println("RefreshToken: " + keycloakInstalled.getRefreshToken());
//    System.out.println("IDToken: " + keycloakInstalled.getIdTokenString());
  }

  @Override
  public void init() throws Exception {

    KEYCLOAK.setLoginResponseWriter(new KeycloakInstalled.HttpResponseWriter() {
      @Override
      public void success(PrintWriter pw, KeycloakInstalled ki) {

        pw.println("HTTP/1.1 200 OK");
        pw.println();

        pw.println("<html><body><script>window.close()</script><h1>Login complete.</h1><div>Please <a href=\"#\" onclick=\"window.close();return false;\">close</a> this browser tab.</div></body></html>");
      }

      @Override
      public void failure(PrintWriter pw, KeycloakInstalled ki) {
        // TODO handle failure
      }
    });

    KEYCLOAK.setLogoutResponseWriter(new KeycloakInstalled.HttpResponseWriter() {
      @Override
      public void success(PrintWriter pw, KeycloakInstalled ki) {

        pw.println("HTTP/1.1 200 OK");
        pw.println();

        pw.println("<html><body><h1>Logout complete.</h1><div>Please <a href=\"#\" onclick=\"window.close();return false;\">close</a> this browser tab.</div></body></html>");
      }

      @Override
      public void failure(PrintWriter pw, KeycloakInstalled ki) {
        // TODO handle failure
      }
    });


    KEYCLOAK.loginDesktop();

    super.init();

  }
}