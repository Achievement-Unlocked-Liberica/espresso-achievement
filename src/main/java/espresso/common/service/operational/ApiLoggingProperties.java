package espresso.common.service.operational;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for API logging functionality.
 * Allows enabling/disabling the @ApiLogger aspect based on environment.
 */
@Component
@ConfigurationProperties(prefix = "common.service.operational.apilogging")
public class ApiLoggingProperties {
    
    /**
     * Whether API logging is enabled.
     * Default: false (disabled for production safety)
     * Set to true in local/dev environments to enable detailed API request/response logging.
     */
    private boolean enabled = false;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
