package espresso.security.domain.entities;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a JWT token with associated user information.
 * Encapsulates authentication token data and user profile details
 * for authenticated user sessions in the security domain.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTUserToken {
    
    /**
     * The JWT token string containing encoded authentication and authorization claims.
     * Used for authenticating API requests and maintaining user sessions.
     */
    private String token;
    
    /**
     * The type of token, typically "Bearer" for JWT authentication.
     * Indicates the authentication scheme to be used with the token.
     */
    private String tokenType;
    
    /**
     * The date and time when the token expires.
     * After this time, the token becomes invalid and cannot be used for authentication.
     */
    private OffsetDateTime expiresAt;
    
    /**
     * The unique key identifying the user associated with this token.
     * Serves as the primary identifier for user lookups and authorization checks.
     */
    private String userKey;
    
    /**
     * The username of the authenticated user.
     * Used for display purposes and user identification within the application.
     */
    private String username;
    
    /**
     * The email address of the authenticated user.
     * Used for user communication and as an alternative login identifier.
     */
    private String email;
    
    /**
     * The first name of the authenticated user.
     * Part of the user's profile information for personalization and display.
     */
    private String firstName;
    
    /**
     * The last name of the authenticated user.
     * Part of the user's profile information for personalization and display.
     */
    private String lastName;
    
    /**
     * Factory method to create a new JWTUserToken with Bearer token type.
     * Provides a convenient way to construct token instances with all required user information.
     *
     * @param token The JWT token string
     * @param expiresAt The token expiration date and time
     * @param userKey The unique user identifier
     * @param username The user's username
     * @param email The user's email address
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @return A new JWTUserToken instance with Bearer token type
     */
    public static JWTUserToken create(String token, OffsetDateTime expiresAt, String userKey, 
                                     String username, String email, String firstName, String lastName) {
        return new JWTUserToken(
            token,
            "Bearer", 
            expiresAt,
            userKey,
            username,
            email,
            firstName,
            lastName
        );
    }
}
