package espresso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import espresso.security.infrastructure.filters.JWTAuthenticationEntryPoint;
import espresso.security.infrastructure.filters.JWTAuthenticationFilter;

/**
 * Security configuration for Espresso Achievement application
 * Configured with JWT authentication for API protection
 */
@Configuration
@EnableWebSecurity
public class EspressoSecurityConfig {

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for API endpoints (since this is a REST API)
            .csrf(csrf -> csrf.disable())
            
            // Configure session management for stateless JWT authentication
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization
            .authorizeHttpRequests(authz -> authz
                // Allow Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
                // Allow actuator endpoints for health checks
                .requestMatchers("/actuator/**").permitAll()
                // Allow authentication endpoint (login)
                .requestMatchers("/api/cmd/security/auth").permitAll()
                // Allow registration endpoint (register)
                .requestMatchers("/api/cmd/security/register").permitAll()
                // Require authentication for all other API endpoints
                .requestMatchers("/api/**").authenticated()
                // Allow all other requests (static resources, etc.)
                .anyRequest().permitAll()
            )
            
            // Configure exception handling for authentication failures
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            
            // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
