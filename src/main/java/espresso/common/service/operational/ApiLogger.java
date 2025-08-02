package espresso.common.service.operational;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for API method logging.
 * When applied to a method, it will log request details including:
 * - Request timestamp
 * - Endpoint path called
 * - Request header contents
 * - Request body contents
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLogger {
    
    /**
     * Optional description for the logged operation
     */
    String value() default "";
    
    /**
     * Whether to log request headers (default: true)
     */
    boolean logHeaders() default true;
    
    /**
     * Whether to log request body (default: true)
     */
    boolean logBody() default true;
    
    /**
     * Log level to use for the output (default: INFO)
     */
    LogLevel level() default LogLevel.INFO;
    
    enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}
