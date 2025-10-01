package espresso.security.infrastructure.filters;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom authentication entry point for handling JWT authentication failures.
 * Implements Spring Security's AuthenticationEntryPoint to provide consistent
 * error responses when authentication is required but missing or invalid.
 * Returns structured JSON error responses for unauthorized access attempts.
 */
@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Handles authentication failures by returning a standardized JSON error response.
     * Called when a request requires authentication but the provided credentials
     * are missing, expired, or invalid. Sets appropriate HTTP status and content type
     * for consistent API error handling.
     *
     * @param request The HTTP request that failed authentication
     * @param response The HTTP response to populate with error details
     * @param authException The authentication exception that triggered this entry point
     * @throws IOException If I/O operations fail during response writing
     * @throws ServletException If servlet processing fails
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Create error response body
        String jsonResponse = """
            {
                "success": false,
                "error": "Unauthorized",
                "message": "LOCALIZE: JWT TOKEN REQUIRED OR INVALID",
                "timestamp": "%s",
                "path": "%s"
            }
            """.formatted(
                java.time.OffsetDateTime.now().toString(),
                request.getRequestURI()
            );
        
        response.getWriter().write(jsonResponse);
    }
}
