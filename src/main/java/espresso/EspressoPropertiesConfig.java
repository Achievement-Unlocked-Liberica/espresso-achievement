package espresso;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "service.properties.config")
public class EspressoPropertiesConfig {
    
    private String baseUrl;
}
