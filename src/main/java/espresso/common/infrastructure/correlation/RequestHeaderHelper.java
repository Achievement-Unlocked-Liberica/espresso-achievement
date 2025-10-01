package espresso.common.infrastructure.correlation;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Helper class for processing HTTP request headers and setting up request context.
 * Handles correlation ID extraction/generation and any other header-based processing
 * that needs to occur at the start of request processing.
 */
@Component
public class RequestHeaderHelper {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";
    
    /**
     * Process incoming request headers and set up request context.
     * Extracts or generates correlation ID and sets up logging context.
     * 
     * @param request The HTTP request
     * @param response The HTTP response for setting response headers
     */
    public void processRequestHeaders(HttpServletRequest request, HttpServletResponse response) {
        // Process correlation ID
        processCorrelationId(request, response);
        
        // Future: Add other header processing here
        // processAuthenticationHeaders(request);
        // processClientInfo(request);
        // processApiVersion(request);
    }
    
    /**
     * Process correlation ID from request header or generate new one.
     * Sets up MDC and thread-local context for the correlation ID.
     * 
     * @param request The HTTP request
     * @param response The HTTP response for setting response headers
     */
    private void processCorrelationId(HttpServletRequest request, HttpServletResponse response) {
        // Get correlation ID from header or generate new one
        String correlationId = getOrGenerateCorrelationId(request);
        
        // Set in MDC for logging
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        
        // Set in response header for client tracing
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        
        // Store in thread-local for application access
        CorrelationContext.setCorrelationId(correlationId);
    }
    
    /**
     * Get correlation ID from request header or generate a new one.
     * 
     * @param request The HTTP request
     * @return The correlation ID (existing or newly generated)
     */
    private String getOrGenerateCorrelationId(HttpServletRequest request) {
        // Try to get from header first (for microservice chains)
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        
        if (correlationId == null || correlationId.trim().isEmpty()) {
            // Generate new correlation ID
            correlationId = UUID.randomUUID().toString();
        }
        
        return correlationId;
    }
    
    /**
     * Clean up request context after request processing.
     * Clears MDC and thread-local context to prevent memory leaks.
     */
    public void cleanupRequestContext() {
        // Clear MDC
        MDC.clear();
        
        // Clear thread-local context
        CorrelationContext.clear();
    }
}