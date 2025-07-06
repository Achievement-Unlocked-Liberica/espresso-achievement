package espresso;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Espresso Achievement application
 * Configured for local development with HTTPS support
 */
@Configuration
@EnableWebSecurity
public class EspressoSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for API endpoints (since this is a REST API)
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization
            .authorizeHttpRequests(authz -> authz
                // Allow Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
                // Allow actuator endpoints for health checks
                .requestMatchers("/actuator/**").permitAll()
                // Allow all other requests for now (no authentication required)
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
