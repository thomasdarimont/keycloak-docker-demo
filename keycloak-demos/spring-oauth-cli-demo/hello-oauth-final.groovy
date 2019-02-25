@Grab('spring-boot-starter-security')
@EnableOAuth2Sso
@RestController
class Application {

  @Value("\${demo.keycloak.accountUrl}")
  String accountUrl;

  @Value("\${demo.keycloak.externalBaseUrl}")
  String externalBaseUrl;

  @Value("\${security.oauth2.client.clientId}")
  String clientId;

  @GetMapping('/account')
  String account(javax.servlet.http.HttpServletResponse response, java.security.Principal user){
    response.sendRedirect(accountUrl+"?referrer=" + clientId + "&referrer_uri=" + externalBaseUrl)
  }

  @GetMapping('/')
  String home(java.security.Principal user) {
    'Hello ' + user.name
  }
}