package espresso.achievement.application.eventHandlers;

 
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.contracts.IAchievementEventHandler;
import espresso.achievement.domain.events.AchievementCelebrationEvent;
import espresso.achievement.domain.events.AchievementCommentEvent;
import espresso.achievement.domain.events.AchievementEvent;
import espresso.achievement.domain.events.AchievementMediaEvent;
import espresso.achievement.infrastructure.integrations.AchievementEventPublisher;

@Component
public class AchievementEventHandler implements IAchievementEventHandler {

    private final AchievementEventPublisher achievementEventPublisher;

    /**
     * Constructor for dependency injection.
     * 
     * @param achievementEventPublisher Event publisher for achievement events
     */
    public AchievementEventHandler(AchievementEventPublisher achievementEventPublisher) {
        this.achievementEventPublisher = achievementEventPublisher;
    }

    @Override
    @EventListener
    public void handleEvent(AchievementEvent event) {
        this.achievementEventPublisher.publishEvent(event);
    }

    @Override
    @EventListener
    public void handleEvent(AchievementCelebrationEvent event) {
        this.achievementEventPublisher.publishEvent(event);
    }

    @Override
    @EventListener
    public void handleEvent(AchievementCommentEvent event) {
        this.achievementEventPublisher.publishEvent(event);
    }

    @Override
    @EventListener
    public void handleEvent(AchievementMediaEvent event) {
        this.achievementEventPublisher.publishEvent(event);
    }

}
