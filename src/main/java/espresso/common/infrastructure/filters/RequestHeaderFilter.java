package espresso.common.infrastructure.filters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import espresso.common.infrastructure.correlation.CorrelationContext;
import espresso.common.infrastructure.correlation.RequestHeaderHelper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Generic request filter for processing HTTP headers and setting up request context.
 * This filter handles various header-based processing including correlation IDs,
 * request tracing, and other request metadata that needs to be available
 * throughout the request lifecycle.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestHeaderFilter implements Filter {
    
    private static final Logger log = LoggerFactory.getLogger(RequestHeaderFilter.class);
    
    private final RequestHeaderHelper headerHelper;
    
    public RequestHeaderFilter(RequestHeaderHelper headerHelper) {
        this.headerHelper = headerHelper;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            // Process headers and set up request context
            headerHelper.processRequestHeaders(httpRequest, httpResponse);
            
            log.trace("Request started with correlation ID: {}", 
                CorrelationContext.getCorrelationId());
            
            chain.doFilter(request, response);
            
        } finally {
            // Clean up request context
            headerHelper.cleanupRequestContext();
            
            log.trace("Request completed with correlation ID: {}", 
                CorrelationContext.getCorrelationId());
        }
    }
}