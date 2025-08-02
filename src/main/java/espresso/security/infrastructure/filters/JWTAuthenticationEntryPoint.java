package espresso.security.infrastructure.filters;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

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
