package org.woehlke.javaee7.petclinic.web.keycloak;

import org.jboss.logging.Logger;
import org.keycloak.adapters.servlet.KeycloakOIDCFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(value = "/*", asyncSupported = true)
public class KeycloakSsoFilter extends KeycloakOIDCFilter {

	private static final Logger LOG = Logger.getLogger(KeycloakSsoFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		LOG.infof("Filtering request: %s", ((HttpServletRequest) req).getRequestURI());

		super.doFilter(req, res, chain);
	}
}