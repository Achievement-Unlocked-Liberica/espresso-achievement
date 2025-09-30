package espresso.achievement.configuration;

 
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import espresso.achievement.infrastructure.integrations.AchievementQueueNameResolver;
import espresso.common.infrastructure.integrations.CommonQueueIntegration;

/**
 * Configuration class that registers the achievement module's queue name resolver
 * with the common queue integration during application startup.
 */
@Component
public class AchievementQueueConfiguration implements CommandLineRunner {

    private final CommonQueueIntegration queueIntegration;
    private final AchievementQueueNameResolver achievementResolver;

    /**
     * Constructor for dependency injection.
     * 
     * @param queueIntegration Common queue integration service
     * @param achievementResolver Achievement queue name resolver
     */
    public AchievementQueueConfiguration(
            CommonQueueIntegration queueIntegration,
            AchievementQueueNameResolver achievementResolver) {
        this.queueIntegration = queueIntegration;
        this.achievementResolver = achievementResolver;
    }

    /**
     * Registers the achievement queue name resolver with the common queue integration.
     * This is called automatically during Spring Boot application startup.
     */
    @Override
    public void run(String... args) throws Exception {
        // Register the achievement module's resolver
        queueIntegration.registerResolver("achievement-module", achievementResolver);
    }
}
