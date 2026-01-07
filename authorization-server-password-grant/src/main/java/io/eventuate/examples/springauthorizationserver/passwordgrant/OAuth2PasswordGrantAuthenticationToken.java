package io.eventuate.examples.springauthorizationserver.passwordgrant;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.io.Serial;
import java.util.Set;

import static io.eventuate.examples.springauthorizationserver.passwordgrant.OAuth2PasswordGrantAuthenticationConverter.PASSWORD_GRANT_TYPE;

/**
 * Authentication token for the OAuth 2.0 Resource Owner Password Credentials Grant.
 *
 * @author Attoumane AHAMADI
 */
public class OAuth2PasswordGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 7840626509676504832L;
    private final String username;
    private final String password;
    private final String clientId;
    private final Set<String> scopes;


    public OAuth2PasswordGrantAuthenticationToken(String username, String password, Authentication clientPrincipal, Set<String> scopes) {
        super(PASSWORD_GRANT_TYPE, clientPrincipal, null);
        this.password = password;
        this.username = username;
        this.clientId = clientPrincipal.getName();
        this.scopes = scopes;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public Set<String> getScopes() {
        return scopes;
    }
}
