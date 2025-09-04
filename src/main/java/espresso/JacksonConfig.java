package espresso;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson configuration to handle Hibernate proxy serialization issues.
 * 
 * This configuration adds the Hibernate5Module to Jackson's ObjectMapper,
 * which properly handles Hibernate lazy-loaded proxies during JSON serialization,
 * preventing errors like "No serializer found for class ByteBuddyInterceptor".
 */
@Configuration
public class JacksonConfig {

    /**
     * Configures Jackson ObjectMapper with Hibernate5Module to handle
     * Hibernate proxy serialization.
     * 
     * @return Configured ObjectMapper with Hibernate support
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register Hibernate5Module to handle proxy serialization
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        
        // Configure the module to avoid forcing lazy loading
        // This means uninitialized proxies will be serialized as null
        hibernate5Module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        hibernate5Module.disable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
        
        mapper.registerModule(hibernate5Module);
        
        // Register JavaTimeModule to handle Java 8 date/time types
        mapper.registerModule(new JavaTimeModule());
        
        return mapper;
    }
}
