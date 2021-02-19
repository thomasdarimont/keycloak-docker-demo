package demo.userdata.web;

import demo.userdata.CustomUserData;
import demo.userdata.CustomUserDataService;
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
class CustomUserDataController {

    private final CustomUserDataService customUserDataService;

    @GetMapping("/current")
    CustomUserData getDataForCurrentUser() {
        return customUserDataService.getDataForCurrentUser();
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    CustomUserData getDataForCurrentUser(@PathVariable String customerId) {
        return customUserDataService.getDataForCustomer(customerId);
    }
}
