package demo.userdata.web;

import demo.userdata.KeycloakUserData;
import demo.userdata.KeycloakUserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Example API for retrieving additional user data
 */
@RestController
@RequestMapping("/api/userdata")
@RequiredArgsConstructor
class KeycloakUserDataController {

    private final KeycloakUserDataService keycloakUserDataService;

    @GetMapping("/current")
    KeycloakUserData getDataForCurrentUser() {
        return keycloakUserDataService.getDataForCurrentUser();
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('SERVICE')")
    KeycloakUserData getDataForCurrentUser(@PathVariable String customerId) {
        return keycloakUserDataService.getDataForCustomer(customerId);
    }
}
