package demo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
class ResourcesApiController {

    private final OAuth2RestOperations oauth2RestTemplate;
    private final ServerProperties serverProperties;

    @GetMapping("{id}")
    @PreAuthorize("hasPermission(#id, 'resource', 'read')")
    Map<String, Object> getResourceByIdForUser(@PathVariable String id) {

        Map<String, Object> resource = new HashMap<>();
        resource.put("id", id);
        resource.put("data", Instant.now());
        resource.put("requestor", SecurityContextHolder.getContext().getAuthentication().getName());

//        ResponseEntity<Resources> todos = oauth2RestTemplate.getForEntity("http://localhost:20000/todos/search/my-todos", Resources.class);
//        resource.put("todos", todos.getBody());

        return resource;
    }

    @GetMapping("{id}/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasPermission(#id, 'resource', 'read')")
    Map<String, Object> getResourceByIdForAdmin(@PathVariable String id) {

        Map<String, Object> resource = getResourceByIdForUser(id);
        resource.put("admin", true);

        return resource;
    }
}