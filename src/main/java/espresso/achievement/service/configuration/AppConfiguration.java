package espresso.achievement.service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "espresso.achivement")
public class AppConfiguration {

    private CloudConfig cloudConfig;

    @Getter
    @Setter
    public static class CloudConfig {

        public String storageUrl;
    }
}
