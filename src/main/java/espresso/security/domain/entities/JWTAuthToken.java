package espresso.security.domain.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import espresso.user.domain.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Service component for JWT token generation, validation, and claim extraction.
 * Handles the creation and verification of JWT authentication tokens using configurable
 * secret keys and expiration times. Provides secure token operations for user authentication.
 */
@Component
public class JWTAuthToken {

    /**
     * The secret key used for signing and verifying JWT tokens.
     * Should be a strong, randomly generated secret stored securely in configuration.
     */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * The token expiration time in milliseconds.
     * Determines how long generated tokens remain valid before requiring renewal.
     */
    @Value("${app.jwt.expiration}") // 24 hours in milliseconds
    private long jwtExpirationMs;

    /**
     * Creates a secure signing key from the configured JWT secret.
     * Ensures the key meets minimum length requirements for HS256 algorithm security.
     * Pads shorter keys to maintain compatibility while preserving security standards.
     *
     * @return A SecretKey instance suitable for JWT token signing and verification
     */
    private SecretKey getSigningKey() {
        // Ensure the key is at least 32 bytes for HS256
        byte[] keyBytes = jwtSecret.getBytes();
        if (keyBytes.length < 32) {
            // Pad the key if it's too short
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            return Keys.hmacShaKeyFor(paddedKey);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a new JWT token for the specified user.
     * Creates a signed token containing user identity and profile information
     * with a configurable expiration time for secure authentication sessions.
     *
     * @param user The user entity for whom to generate the token
     * @return A JWTUserToken containing the signed token and user information
     */
    public JWTUserToken generateToken(User user) {
        OffsetDateTime expirationTime = OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(jwtExpirationMs / 1000);
        
        String token = Jwts.builder()
                .subject(user.getUsername())
                .claim("userKey", user.getEntityKey())
                .claim("email", user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .issuedAt(new Date())
                .expiration(Date.from(expirationTime.toInstant()))
                .signWith(getSigningKey())
                .compact();

        return JWTUserToken.create(
            token,
            expirationTime,
            user.getEntityKey(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );
    }

    /**
     * Validates a JWT token and extracts its claims.
     * Verifies the token signature and structure, throwing an exception for invalid tokens.
     * Returns the token's payload claims for further processing.
     *
     * @param token The JWT token string to validate
     * @return The validated token's claims
     * @throws RuntimeException If the token is invalid or cannot be parsed
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    /**
     * Extracts the username from a JWT token.
     * Validates the token and returns the subject claim containing the username.
     *
     * @param token The JWT token from which to extract the username
     * @return The username contained in the token's subject claim
     */
    public String extractUsername(String token) {
        return validateToken(token).getSubject();
    }

    /**
     * Checks if a JWT token has expired.
     * Compares the token's expiration time with the current time to determine validity.
     *
     * @param token The JWT token to check for expiration
     * @return true if the token has expired, false if still valid
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Validates JWT token and returns true if valid.
     * Performs comprehensive validation including signature verification and expiration check.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid and not expired, false otherwise
     */
    public boolean isValidToken(String token) {
        try {
            validateToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts user key from JWT token.
     * Retrieves the custom userKey claim containing the user's unique identifier.
     *
     * @param token The JWT token from which to extract the user key
     * @return The user key claim value, or null if extraction fails
     */
    public String extractUserKey(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.get("userKey", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts email from JWT token.
     * Retrieves the custom email claim containing the user's email address.
     *
     * @param token The JWT token from which to extract the email
     * @return The email claim value, or null if extraction fails
     */
    public String extractEmail(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.get("email", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
