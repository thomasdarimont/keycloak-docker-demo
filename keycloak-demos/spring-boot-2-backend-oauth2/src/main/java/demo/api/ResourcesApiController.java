package demo.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("{id}")
    @PreAuthorize("hasPermission(#id, 'resource', 'read')")
    Map<String, Object> getResourceByIdForUser(@PathVariable String id) {

        Map<String, Object> resource = new HashMap<>();
        resource.put("id", id);
        resource.put("data", Instant.now());
        resource.put("requestor", SecurityContextHolder.getContext().getAuthentication().getName());

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