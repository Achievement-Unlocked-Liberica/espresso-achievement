package espresso.security.domain.entities;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

/**
 * Custom authentication token that extends Spring Security's UsernamePasswordAuthenticationToken
 * to include additional user information from JWT tokens.
 * Provides access to user key and email claims extracted from validated JWT tokens
 * for enhanced authentication context within the security framework.
 */
@Getter
public class JWTAuthenticationToken extends UsernamePasswordAuthenticationToken {

    /**
     * The unique key identifying the authenticated user.
     * Extracted from the JWT token's userKey claim for user lookups and authorization.
     */
    private final String userKey;
    
    /**
     * The email address of the authenticated user.
     * Extracted from the JWT token's email claim for user identification and communication.
     */
    private final String email;
    
    /**
     * Creates a new JWT-based authentication token with user details.
     * Extends the standard Spring Security authentication token to include
     * additional user context from JWT token claims.
     *
     * @param principal The principal object (typically username)
     * @param credentials The credentials object (typically password, may be null for JWT)
     * @param authorities The collection of granted authorities for this user
     * @param userKey The unique user identifier from JWT claims
     * @param email The user's email address from JWT claims
     */
    public JWTAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, 
                                  String userKey, String email) {
        super(principal, credentials, authorities);
        this.userKey = userKey;
        this.email = email;
    }
}
