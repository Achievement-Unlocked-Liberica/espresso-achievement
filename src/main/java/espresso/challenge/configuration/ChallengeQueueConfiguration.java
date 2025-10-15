package espresso.challenge.configuration;

 
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import espresso.challenge.infrastructure.integrations.ChallengeQueueNameResolver;
import espresso.common.infrastructure.integrations.CommonQueueIntegration;

/**
 * Configuration class that registers the challenge module's queue name resolver
 * with the common queue integration during application startup.
 */
@Component
public class ChallengeQueueConfiguration implements CommandLineRunner {

    private final CommonQueueIntegration queueIntegration;
    private final ChallengeQueueNameResolver challengeResolver;

    /**
     * Constructor for dependency injection.
     * 
     * @param queueIntegration Common queue integration service
     * @param challengeResolver Challenge queue name resolver
     */
    public ChallengeQueueConfiguration(
            CommonQueueIntegration queueIntegration,
            ChallengeQueueNameResolver challengeResolver) {
        this.queueIntegration = queueIntegration;
        this.challengeResolver = challengeResolver;
    }

    /**
     * Registers the challenge queue name resolver with the common queue integration.
     * This is called automatically during Spring Boot application startup.
     */
    @Override
    public void run(String... args) throws Exception {
        // Register the challenge module's resolver
        queueIntegration.registerResolver("challenge-module", challengeResolver);
    }
}
