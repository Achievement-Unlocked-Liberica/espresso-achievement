package espresso;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for Espresso Achievement API
 * Configured to work with HTTPS in local development
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Espresso Achievement API")
                        .description("RESTful API for Achievement management system with dual protocol support (HTTP/HTTPS)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Espresso Development Team")
                                .email("dev@espresso.local")))
                .addServersItem(new Server()
                        .url("https://localhost:8443")
                        .description("Secure HTTPS Server (Production-like)"))
                .addServersItem(new Server()
                        .url("http://localhost:8090")
                        .description("HTTP Server (Mobile testing & Development)"));
    }
}
