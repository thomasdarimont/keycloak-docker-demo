package demo.keycloak;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.TokenVerifier;
import org.keycloak.adapters.AdapterTokenStore;
import org.keycloak.adapters.AdapterUtils;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.OIDCHttpFacade;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.RequestAuthenticator;
import org.keycloak.adapters.rotation.AdapterTokenVerifier;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.adapters.springsecurity.token.SpringSecurityTokenStore;
import org.keycloak.common.VerificationException;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.AdapterConstants;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class CustomSpringSecurityCookieTokenStore extends SpringSecurityTokenStore {

    private final Logger logger = LoggerFactory.getLogger(CustomSpringSecurityCookieTokenStore.class);

    public static final String ID_TOKEN_COOKIE = AdapterConstants.KEYCLOAK_ADAPTER_STATE_COOKIE + "_IDT";
    public static final String ACCESS_TOKEN_COOKIE = AdapterConstants.KEYCLOAK_ADAPTER_STATE_COOKIE + "_AT";
    public static final String REFRESH_TOKEN_COOKIE = AdapterConstants.KEYCLOAK_ADAPTER_STATE_COOKIE + "_RT";

    private final KeycloakDeployment deployment;
    private final HttpFacade facade;
    private volatile boolean cookieChecked = false;

    public CustomSpringSecurityCookieTokenStore(
            KeycloakDeployment deployment,
            HttpServletRequest request,
            HttpServletResponse response) {
        super(deployment, request);
        Assert.notNull(response, "HttpServletResponse is required");
        this.deployment = deployment;
        this.facade = new SimpleHttpFacade(request, response);
    }

    @Override
    public void checkCurrentToken() {
        final KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal =
                checkPrincipalFromCookie();
        if (principal != null) {
            final RefreshableKeycloakSecurityContext securityContext =
                    principal.getKeycloakSecurityContext();
            KeycloakSecurityContext current = ((OIDCHttpFacade) facade).getSecurityContext();
            if (current != null) {
                securityContext.setAuthorizationContext(current.getAuthorizationContext());
            }
            final Set<String> roles = AdapterUtils.getRolesFromSecurityContext(securityContext);
            final OidcKeycloakAccount account =
                    new SimpleKeycloakAccount(principal, roles, securityContext);
            SecurityContextHolder.getContext()
                    .setAuthentication(new KeycloakAuthenticationToken(account, false));
        } else {
            super.checkCurrentToken();
        }
        cookieChecked = true;
    }

    @Override
    public boolean isCached(RequestAuthenticator authenticator) {
        if (!cookieChecked) {
            checkCurrentToken();
        }
        return super.isCached(authenticator);
    }

    @Override
    public void refreshCallback(RefreshableKeycloakSecurityContext securityContext) {
        super.refreshCallback(securityContext);
        setTokenCookie(deployment, facade, securityContext);
    }

    protected void setTokenCookie(KeycloakDeployment deployment, HttpFacade facade, RefreshableKeycloakSecurityContext session) {
//        log.debugf("Set new %s cookie now", AdapterConstants.KEYCLOAK_ADAPTER_STATE_COOKIE);
        String accessToken = session.getTokenString();
        String idToken = session.getIdTokenString();
        String refreshToken = session.getRefreshToken();

        String cookiePath = getCookiePath(deployment, facade);
        HttpFacade.Response resp = facade.getResponse();
        boolean secure = deployment.getSslRequired().isRequired(facade.getRequest().getRemoteAddr());
        resp.setCookie(ID_TOKEN_COOKIE, idToken, cookiePath, null, -1, secure, true);
        resp.setCookie(ACCESS_TOKEN_COOKIE, accessToken, cookiePath, null, -1, secure, true);
        resp.setCookie(REFRESH_TOKEN_COOKIE, refreshToken, cookiePath, null, -1, secure, true);
    }

    @Override
    public void saveAccountInfo(OidcKeycloakAccount account) {
        super.saveAccountInfo(account);
        RefreshableKeycloakSecurityContext securityContext =
                (RefreshableKeycloakSecurityContext) account.getKeycloakSecurityContext();
        setTokenCookie(deployment, facade, securityContext);
    }

    @Override
    public void logout() {
        removeCookie(deployment, facade);
        super.logout();
    }

    /**
     * Verify if we already have authenticated and active principal in cookie. Perform refresh if
     * it's not active
     *
     * @return valid principal
     */
    private KeycloakPrincipal<RefreshableKeycloakSecurityContext> checkPrincipalFromCookie() {
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal =
                getPrincipalFromCookie(deployment, facade, this);
        if (principal == null) {
            logger.debug("Account was not in cookie or was invalid");
            return null;
        }

        RefreshableKeycloakSecurityContext session = principal.getKeycloakSecurityContext();

        if (session.isActive() && !session.getDeployment().isAlwaysRefreshToken()) return principal;
        boolean success = session.refreshExpiredToken(false);
        if (success && session.isActive()) {
            refreshCallback(session);
            return principal;
        }

        logger.debug(
                "Cleanup and expire cookie for user {} after failed refresh", principal.getName());
        removeCookie(deployment, facade);
        return null;
    }

    protected KeycloakPrincipal<RefreshableKeycloakSecurityContext> getPrincipalFromCookie(KeycloakDeployment deployment, HttpFacade facade, AdapterTokenStore tokenStore) {

        HttpFacade.Request req = facade.getRequest();
        OIDCHttpFacade.Cookie accessTokenCookie = req.getCookie(ACCESS_TOKEN_COOKIE);
        OIDCHttpFacade.Cookie refreshTokenCookie = req.getCookie(REFRESH_TOKEN_COOKIE);
        OIDCHttpFacade.Cookie idTokenCookie = req.getCookie(ID_TOKEN_COOKIE);
        if (accessTokenCookie == null || refreshTokenCookie == null || idTokenCookie == null) {
//            log.debug("Not found adapter state cookie in current request");
            return null;
        }

        String accessTokenString = accessTokenCookie.getValue();
        String idTokenString = idTokenCookie.getValue();
        String refreshTokenString = refreshTokenCookie.getValue();

        try {
            // Skip check if token is active now. It's supposed to be done later by the caller
            TokenVerifier<AccessToken> tokenVerifier = AdapterTokenVerifier.createVerifier(accessTokenString, deployment, true, AccessToken.class)
                    .checkActive(false)
                    .verify();
            AccessToken accessToken = tokenVerifier.getToken();

            IDToken idToken;
            if (idTokenString != null && idTokenString.length() > 0) {
                try {
                    JWSInput input = new JWSInput(idTokenString);
                    idToken = input.readJsonContent(IDToken.class);
                } catch (JWSInputException e) {
                    throw new VerificationException(e);
                }
            } else {
                idToken = null;
            }

//            log.debug("Token Verification succeeded!");
            RefreshableKeycloakSecurityContext secContext = new RefreshableKeycloakSecurityContext(deployment, tokenStore, accessTokenString, accessToken, idTokenString, idToken, refreshTokenString);
            return new KeycloakPrincipal<>(AdapterUtils.getPrincipalName(deployment, accessToken), secContext);
        } catch (VerificationException ve) {
//            log.warn("Failed verify token", ve);
            return null;
        }
    }

    protected void removeCookie(KeycloakDeployment deployment, HttpFacade facade) {
        String cookiePath = getCookiePath(deployment, facade);
        HttpFacade.Response resp = facade.getResponse();
        resp.resetCookie(ID_TOKEN_COOKIE, cookiePath);
        resp.resetCookie(ACCESS_TOKEN_COOKIE, cookiePath);
        resp.resetCookie(REFRESH_TOKEN_COOKIE, cookiePath);
    }

    protected String getCookiePath(KeycloakDeployment deployment, HttpFacade facade) {
        String path = deployment.getAdapterStateCookiePath() == null ? "" : deployment.getAdapterStateCookiePath().trim();
        if (path.startsWith("/")) {
            return path;
        }
        String contextPath = getContextPath(facade);
        StringBuilder cookiePath = new StringBuilder(contextPath);
        if (!contextPath.endsWith("/") && !path.isEmpty()) {
            cookiePath.append("/");
        }
        return cookiePath.append(path).toString();
    }

    protected String getContextPath(HttpFacade facade) {
        String uri = facade.getRequest().getURI();
        String path = KeycloakUriBuilder.fromUri(uri).getPath();
        if (path == null || path.isEmpty()) {
            return "/";
        }
        int index = path.indexOf("/", 1);
        return index == -1 ? path : path.substring(0, index);
    }
}
