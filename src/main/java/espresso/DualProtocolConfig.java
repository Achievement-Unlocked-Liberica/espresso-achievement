package espresso;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dual protocol configuration for Espresso Achievement Application
 * Enables both HTTP and HTTPS access simultaneously
 * 
 * - HTTPS: 8443 (primary with SSL certificate)
 * - HTTP: 8080 (for mobile testing and development)
 */
@Configuration
public class DualProtocolConfig {

    /**
     * Configure Tomcat to support both HTTP and HTTPS
     * HTTPS is configured via application.properties (port 8443)
     * HTTP is configured here (port 8080)
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        
        // Add HTTP connector on port 8080
        tomcat.addAdditionalTomcatConnectors(createHttpConnector());
        
        return tomcat;
    }

    /**
     * Create HTTP connector for port 8080
     * This allows mobile apps and clients that have issues with self-signed certificates
     * to connect via plain HTTP
     */
    private Connector createHttpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        return connector;
    }
}
