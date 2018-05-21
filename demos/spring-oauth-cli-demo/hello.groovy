

@Grab('spring-boot-starter-security')
@RestController
class Application {

  @RequestMapping('/')
  String home(java.security.Principal user ) {
    'Hello ' + user.name
  }
}
