package espresso.common.service.operational;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aspect for handling @ApiLogger annotation functionality.
 * Provides colorful logging of API requests with detailed information.
 */
@Aspect
@Component
public class ApiLoggerAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggerAspect.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ApiLoggingProperties apiLoggingProperties;
    
    // ANSI color codes for colorful logging
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String WHITE = "\u001B[37m";

    @Around("@annotation(apiLogger)")
    public Object logApiCall(ProceedingJoinPoint joinPoint, ApiLogger apiLogger) throws Throwable {
        // Check if API logging is enabled
        if (!apiLoggingProperties.isEnabled()) {
            // If logging is disabled, just proceed with the method execution without logging
            return joinPoint.proceed();
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            logRequest(joinPoint, apiLogger);
            Object result = joinPoint.proceed();
            logResponse(joinPoint, apiLogger, result, startTime);
            return result;
        } catch (Exception e) {
            logError(joinPoint, apiLogger, e, startTime);
            throw e;
        }
    }

    private void logRequest(ProceedingJoinPoint joinPoint, ApiLogger apiLogger) {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n").append(CYAN).append("API REQUEST:").append(RESET);
        
        // Timestamp
        logMessage.append("\n").append(YELLOW).append("Timestamp: ").append(RESET).append(WHITE).append(timestamp).append(RESET);
        
        // Method and Class
        String methodInfo = className + "." + methodName + "()";
        logMessage.append("\n").append(YELLOW).append("Method: ").append(RESET).append(WHITE).append(methodInfo).append(RESET);
        
        // HTTP Method and Path
        String httpMethod = request.getMethod();
        String requestPath = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = requestPath + (queryString != null ? "?" + queryString : "");
        
        logMessage.append("\n").append(YELLOW).append("Endpoint: ").append(RESET)
                 .append(getHttpMethodColor(httpMethod)).append(httpMethod).append(RESET)
                 .append(" ").append(WHITE).append(fullPath).append(RESET);

        // Description if provided
        if (!apiLogger.value().isEmpty()) {
            logMessage.append("\n").append(YELLOW).append("Description: ").append(RESET)
                     .append(WHITE).append(apiLogger.value()).append(RESET);
        }

        // Headers
        if (apiLogger.logHeaders()) {
            logMessage.append("\n").append(YELLOW).append("Headers:").append(RESET);
            
            Map<String, String> headers = getRequestHeaders(request);
            if (headers.isEmpty()) {
                logMessage.append("\n  ").append(MAGENTA).append("(No headers)").append(RESET);
            } else {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    logMessage.append("\n  ").append(MAGENTA).append(header.getKey()).append(": ").append(RESET)
                             .append(WHITE).append(header.getValue()).append(RESET);
                }
            }
        }

        // Request Body
        if (apiLogger.logBody()) {
            logMessage.append("\n").append(YELLOW).append("Request Body:").append(RESET);
            
            String body = getRequestBody(request, joinPoint.getArgs());
            if (body == null || body.trim().isEmpty()) {
                logMessage.append("\n  ").append(MAGENTA).append("(No body)").append(RESET);
            } else {
                String[] bodyLines = body.split("\n");
                for (String line : bodyLines) {
                    logMessage.append("\n  ").append(WHITE).append(line).append(RESET);
                }
            }
        }
        
        logWithLevel(apiLogger.level(), logMessage.toString());
    }

    private void logResponse(ProceedingJoinPoint joinPoint, ApiLogger apiLogger, Object result, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n").append(GREEN).append("API RESPONSE:").append(RESET);
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodInfo = className + "." + methodName + "()";
        
        logMessage.append("\n").append(YELLOW).append("Method: ").append(RESET).append(WHITE).append(methodInfo).append(RESET);
        logMessage.append("\n").append(YELLOW).append("Duration: ").append(RESET).append(WHITE).append(duration + "ms").append(RESET);
        logMessage.append("\n").append(YELLOW).append("Status: ").append(RESET).append(GREEN).append("SUCCESS").append(RESET);
        
        logWithLevel(apiLogger.level(), logMessage.toString());
    }

    private void logError(ProceedingJoinPoint joinPoint, ApiLogger apiLogger, Exception e, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\n").append(RED).append("API ERROR:").append(RESET);
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodInfo = className + "." + methodName + "()";
        
        logMessage.append("\n").append(YELLOW).append("Method: ").append(RESET).append(WHITE).append(methodInfo).append(RESET);
        logMessage.append("\n").append(YELLOW).append("Duration: ").append(RESET).append(WHITE).append(duration + "ms").append(RESET);
        
        String errorType = e.getClass().getSimpleName();
        logMessage.append("\n").append(YELLOW).append("Error: ").append(RESET).append(RED).append(errorType).append(RESET);
        
        String errorMessage = e.getMessage();
        if (errorMessage != null && !errorMessage.isEmpty()) {
            logMessage.append("\n").append(YELLOW).append("Message: ").append(RESET).append(WHITE).append(errorMessage).append(RESET);
        }
        
        logger.error(logMessage.toString());
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                
                // Mask sensitive headers
                if (headerName.toLowerCase().contains("authorization") || 
                    headerName.toLowerCase().contains("password") ||
                    headerName.toLowerCase().contains("token")) {
                    headerValue = "***MASKED***";
                }
                
                headers.put(headerName, headerValue);
            }
        }
        
        return headers;
    }

    private String getRequestBody(HttpServletRequest request, Object[] args) {
        try {
            // Try to get body from method arguments (common in REST controllers)
            for (Object arg : args) {
                if (arg != null && !isPrimitiveOrWrapper(arg.getClass()) && !arg.getClass().equals(String.class)) {
                    // Skip HttpServletRequest, HttpServletResponse, and other Spring objects
                    if (!arg.getClass().getName().startsWith("javax.servlet") &&
                        !arg.getClass().getName().startsWith("org.springframework")) {
                        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arg);
                    }
                }
            }
            
            // If no suitable object found in args, try to read from request
            if ("POST".equalsIgnoreCase(request.getMethod()) || 
                "PUT".equalsIgnoreCase(request.getMethod()) || 
                "PATCH".equalsIgnoreCase(request.getMethod())) {
                
                try {
                    String body = request.getReader().lines().collect(Collectors.joining("\n"));
                    if (!body.trim().isEmpty()) {
                        return body;
                    }
                } catch (Exception e) {
                    // Request body might have already been read
                }
            }
            
            return null;
        } catch (Exception e) {
            return "Error reading request body: " + e.getMessage();
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz.equals(Boolean.class) || clazz.equals(Integer.class) || clazz.equals(Character.class) ||
               clazz.equals(Byte.class) || clazz.equals(Short.class) || clazz.equals(Double.class) ||
               clazz.equals(Long.class) || clazz.equals(Float.class);
    }

    private String getHttpMethodColor(String method) {
        switch (method.toUpperCase()) {
            case "GET": return GREEN;
            case "POST": return BLUE;
            case "PUT": return YELLOW;
            case "DELETE": return RED;
            case "PATCH": return MAGENTA;
            default: return RESET;
        }
    }

    private void logWithLevel(ApiLogger.LogLevel level, String message) {
        switch (level) {
            case DEBUG:
                logger.debug(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
                logger.error(message);
                break;
        }
    }
}
