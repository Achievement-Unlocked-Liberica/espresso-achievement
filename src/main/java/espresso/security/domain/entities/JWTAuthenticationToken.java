package espresso.security.domain.entities;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

@Getter
public class JWTAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String userKey;
    private final String email;
    
    public JWTAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, 
                                  String userKey, String email) {
        super(principal, credentials, authorities);
        this.userKey = userKey;
        this.email = email;
    }
}
