package demo.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import demo.config.KeycloakLinkGenerator;
import demo.todo.Todo;
import demo.todo.TodoClient;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
class UiController {

	private final TodoClient todoClient;

	private final KeycloakLinkGenerator keycloakLinkGenerator;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) throws Exception {
		request.logout();
		return "redirect:/todos";
	}

	@GetMapping("/account")
	public String account() {

		String todoUri = linkTo(getClass()).toUriComponentsBuilder().path("/todos").toUriString();
		return "redirect:" + keycloakLinkGenerator.getAccountLinkWithBacklink(todoUri);
	}

	@GetMapping("/todos*")
	public String todos(Model model) {

		Resources<Resource<Todo>> todos = todoClient.fetchTodos();
		model.addAttribute("todos", todos.getContent());

		return "todos";
	}
}
