package demo.web;

import demo.keycloak.KeycloakLinkGenerator;
import demo.todo.Todo;
import demo.todo.TodoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequiredArgsConstructor
class UiController {

    private final TodoClient todoClient;

    private final KeycloakLinkGenerator keycloakLinkGenerator;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/account")
    public String account() {

        String todoUri = linkTo(getClass()).toUriComponentsBuilder().path("/todos").toUriString();
        return "redirect:" + keycloakLinkGenerator.createAccountLinkWithBacklink(todoUri);
    }

    @GetMapping("/todos*")
    public String todos(Model model, @AuthenticationPrincipal Authentication currentUser) {

        CollectionModel<EntityModel<Todo>> todos = todoClient.fetchTodos();
        model.addAttribute("todos", todos.getContent());

        System.out.printf("Current user roles: %s%n", currentUser.getAuthorities());

        return "todos";
    }
}
