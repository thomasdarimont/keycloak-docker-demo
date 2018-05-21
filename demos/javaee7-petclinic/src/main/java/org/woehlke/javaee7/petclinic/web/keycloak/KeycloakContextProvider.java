package org.woehlke.javaee7.petclinic.web.keycloak;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;

@RequestScoped
public class KeycloakContextProvider {

	@Produces
	public RefreshableKeycloakSecurityContext getCurrentKeycloakSecurityContext() {
		return getKeycloakSecurityContext(getCurrentHttpRequest());
	}

	private RefreshableKeycloakSecurityContext getKeycloakSecurityContext(HttpServletRequest httpRequest) {
		return (RefreshableKeycloakSecurityContext) httpRequest.getAttribute(KeycloakSecurityContext.class.getName());
	}

	private HttpServletRequest getCurrentHttpRequest() {

		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();

		return (HttpServletRequest) externalContext.getRequest();
	}
}
