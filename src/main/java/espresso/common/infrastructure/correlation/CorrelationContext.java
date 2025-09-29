package espresso.common.infrastructure.correlation;

/**
 * Thread-local context holder for correlation ID and other request-scoped data.
 * Provides safe access to correlation information throughout the request lifecycle
 * without needing to pass parameters through every method call.
 */
public class CorrelationContext {
    
    private static final ThreadLocal<String> correlationId = new ThreadLocal<>();
    
    /**
     * Set the correlation ID for the current thread/request.
     * 
     * @param id The correlation ID to set
     */
    public static void setCorrelationId(String id) {
        correlationId.set(id);
    }
    
    /**
     * Get the correlation ID for the current thread/request.
     * 
     * @return The correlation ID, or null if not set
     */
    public static String getCorrelationId() {
        return correlationId.get();
    }
    
    /**
     * Clear the correlation ID for the current thread.
     * Should be called at the end of request processing to prevent memory leaks.
     */
    public static void clear() {
        correlationId.remove();
    }
    
    /**
     * Check if correlation ID is set for the current thread.
     * 
     * @return true if correlation ID is set, false otherwise
     */
    public static boolean hasCorrelationId() {
        return correlationId.get() != null;
    }
}