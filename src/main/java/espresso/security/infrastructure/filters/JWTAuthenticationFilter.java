package espresso.security.infrastructure.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import espresso.security.domain.entities.JWTAuthToken;
import espresso.security.domain.entities.JWTAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT authentication filter that processes incoming HTTP requests to validate JWT tokens.
 * Extends Spring Security's OncePerRequestFilter to ensure authentication is processed
 * exactly once per request. Extracts and validates JWT tokens from Authorization headers,
 * sets up the security context for authenticated users, and allows unauthenticated
 * access to authentication endpoints.
 */
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT token service for token validation and claim extraction.
     * Used to verify token authenticity and extract user information.
     */
    @Autowired
    private JWTAuthToken jwtAuthToken;

    /**
     * Spring Security user details service for loading user information.
     * Provides user details for authentication context setup after token validation.
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Processes each HTTP request to validate JWT tokens and set up authentication context.
     * Skips authentication for login endpoints, extracts tokens from Authorization headers,
     * validates tokens, and establishes security context for authenticated requests.
     *
     * @param request The HTTP servlet request containing potential JWT tokens
     * @param response The HTTP servlet response for the request
     * @param filterChain The filter chain to continue processing the request
     * @throws ServletException If servlet processing fails
     * @throws IOException If I/O operations fail during request processing
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        // Skip authentication for the login endpoint
        String requestPath = request.getRequestURI();
        if (requestPath.contains("/api/cmd/security/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            try {
                username = jwtAuthToken.extractUsername(token);
            } catch (Exception e) {
                logger.warn("Unable to extract username from JWT token: " + e.getMessage());
            }
        }

        // Validate token and set authentication context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            if (jwtAuthToken.isValidToken(token)) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // Extract JWT claims
                    String userKey = jwtAuthToken.extractUserKey(token);
                    String email = jwtAuthToken.extractEmail(token);
                    
                    JWTAuthenticationToken authenticationToken = 
                        new JWTAuthenticationToken(userDetails, null, userDetails.getAuthorities(), userKey, email);
                    
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                } catch (Exception e) {
                    logger.warn("User not found or authentication failed: " + e.getMessage());
                }
            } else {
                logger.warn("Invalid JWT token");
            }
        }

        filterChain.doFilter(request, response);
    }
}
