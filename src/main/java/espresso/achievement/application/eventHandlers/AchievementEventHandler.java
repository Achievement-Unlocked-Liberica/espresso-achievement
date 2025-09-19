package espresso.achievement.application.eventHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.contracts.IAchievementEventHandler;
import espresso.achievement.domain.events.AchievementCelebrationEvent;
import espresso.achievement.domain.events.AchievementCommentEvent;
import espresso.achievement.domain.events.AchievementEvent;
import espresso.achievement.infrastructure.integrations.AchievementEventPublisher;

@Component
public class AchievementEventHandler implements IAchievementEventHandler {

    @Autowired
    private AchievementEventPublisher achievementEventPublisher;

    @Override
    @EventListener
    public void handleEvent(AchievementEvent event) {
        System.out.println("EVENT HANDLED: " + event.toString());

        this.achievementEventPublisher.publishEvent(event);
    }

    @Override
    @EventListener
    public void handleEvent(AchievementCelebrationEvent event) {
        System.out.println("EVENT HANDLED: " + event.toString());

        this.achievementEventPublisher.publishEvent(event);
    }

    @Override
    @EventListener
    public void handleEvent(AchievementCommentEvent event) {
        System.out.println("EVENT HANDLED: " + event.toString());

        this.achievementEventPublisher.publishEvent(event);
    }

}
