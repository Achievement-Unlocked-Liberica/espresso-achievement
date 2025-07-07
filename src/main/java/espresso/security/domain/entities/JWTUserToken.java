package espresso.security.domain.entities;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTUserToken {
    
    private String token;
    private String tokenType;
    private OffsetDateTime expiresAt;
    private String userKey;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    
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
