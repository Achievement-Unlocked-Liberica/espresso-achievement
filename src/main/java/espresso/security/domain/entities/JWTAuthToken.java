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

@Component
public class JWTAuthToken {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}") // 24 hours in milliseconds
    private long jwtExpirationMs;

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

    public String extractUsername(String token) {
        return validateToken(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = validateToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Validates JWT token and returns true if valid
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
     * Extracts user key from JWT token
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
     * Extracts email from JWT token
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
