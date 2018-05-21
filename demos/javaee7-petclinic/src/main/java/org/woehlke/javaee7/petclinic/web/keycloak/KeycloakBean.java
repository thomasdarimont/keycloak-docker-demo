package org.woehlke.javaee7.petclinic.web.keycloak;

import org.keycloak.adapters.AdapterUtils;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.keycloak.representations.IDToken;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;

@ManagedBean(name = "keycloak")
@SessionScoped
public class KeycloakBean {

	@Inject
	RefreshableKeycloakSecurityContext securityContext;

	@Inject
	HttpServletRequest currentRequest;

	public String getUserId() {
		return securityContext.getIdToken().getSubject();
	}

	public String getUsername() {
		return securityContext.getIdToken().getPreferredUsername();
	}

	public String getUserDisplayName() {
		return securityContext.getIdToken().getName();
	}

	public Map<String, Object> getCustomAttributes() {
		return getAccessToken().getOtherClaims();
	}

	public AccessToken getAccessToken() {
		return securityContext.getToken();
	}

	public String getAccessTokenString() {
		return securityContext.getTokenString();
	}

	private IDToken getIdToken() {
		return securityContext.getIdToken();
	}

	public Set<String> getRoles() {

		return Optional //
				.ofNullable(AdapterUtils.getRolesFromSecurityContext(securityContext)) //
				.orElseGet(Collections::emptySet);
	}

	public Set<String> getRealmRoles() {

		return Optional //
				.ofNullable(securityContext.getToken().getRealmAccess()) //
				.map(Access::getRoles) //
				.orElseGet(Collections::emptySet);
	}

	public Map<String, Set<String>> getSiblingRoles() {

		String currentClientName = securityContext.getDeployment().getResourceName();
		Predicate<Map.Entry<String, Access>> onlyOtherClients = //
				entry -> !entry.getKey().equals(currentClientName);

		Map<String, Set<String>> roles = securityContext //
				.getToken() //
				.getResourceAccess().entrySet().stream() //
				.filter(onlyOtherClients) //
				.collect(toMap(Map.Entry::getKey, e -> e.getValue().getRoles()));

		return roles;
	}

	public void logout() throws Exception {

		currentRequest.logout();

		String logoutUri = securityContext.getDeployment()//
				.getLogoutUrl() //
				.queryParam("redirect_uri", getContextRootUri(currentRequest)) //
				.toTemplate();

		ExternalContext externalContext = FacesContext.getCurrentInstance() //
				.getExternalContext();
		externalContext.invalidateSession();
		externalContext.redirect(logoutUri);
	}

	public String getAccountUri() {

		KeycloakDeployment deployment = securityContext.getDeployment();

		String accountUrl = KeycloakUriBuilder.fromUri(deployment.getAccountUrl()) //
				.queryParam("referrer", deployment.getResourceName()) //
				.queryParam("referrer_uri", getContextRootUri(currentRequest)) //
				.toTemplate();

		return accountUrl;
	}

	private String getContextRootUri(HttpServletRequest request) {
		return URI.create(request.getRequestURL().toString()).resolve(request.getContextPath()).toString();
	}
}