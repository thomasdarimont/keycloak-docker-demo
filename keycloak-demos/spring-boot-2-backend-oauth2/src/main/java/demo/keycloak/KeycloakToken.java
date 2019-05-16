package demo.keycloak;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides easy access to JWT claims
 */
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakToken {

    String iss;

    String azp;

    String sub;

    String typ;

    @JsonProperty("aud")
    Set<String> audience;

    @JsonProperty("allowed-origins")
    Set<String> allowedOrigins;

    String scope;

    @JsonProperty("preferred_username")
    String preferredUsername;

    @JsonProperty("given_name")
    String firstname;

    @JsonProperty("family_name")
    String lastname;

    @JsonProperty("name")
    String displayName;

    String email;

    @JsonProperty("realm_access")
    Map<String, Set<String>> realmAccess = new HashMap<>();

    @JsonProperty("resource_access")
    Map<String, Map<String, List<String>>> resourceAccess = new HashMap<>();

    Map<String, Object> additionalClaims = new HashMap<>();

    @JsonAnySetter
    public void setAdditionalClaim(String name, String value) {
        additionalClaims.put(name, value);
    }

    @JsonIgnore
    public Set<String> getScopeItems() {

        if (StringUtils.isEmpty(scope)) {
            return Collections.emptySet();
        }

        return new HashSet<>(Arrays.asList(scope.split(" ")));
    }

    @JsonIgnore
    public Set<String> getRealmRoles() {

        if (CollectionUtils.isEmpty(realmAccess)) {
            return Collections.emptySet();
        }

        Set<String> realmRoles = realmAccess.get("roles");
        if (CollectionUtils.isEmpty(realmRoles)) {
            return Collections.emptySet();
        }

        return realmRoles.stream().map(this::normalizeRole).collect(Collectors.toSet());
    }

    public Set<String> getClientRoles(String clientId) {

        if (CollectionUtils.isEmpty(resourceAccess)) {
            return Collections.emptySet();
        }

        Map<String, List<String>> clientAccess = resourceAccess.get(clientId);
        if (CollectionUtils.isEmpty(clientAccess)) {
            return Collections.emptySet();
        }

        List<String> clientRoles = clientAccess.get("roles");
        if (CollectionUtils.isEmpty(clientRoles)) {
            return Collections.emptySet();
        }

        return clientRoles.stream().map(this::normalizeRole).collect(Collectors.toSet());
    }

    private String normalizeRole(String role) {
        return role.replace('-', '_');
    }
}
