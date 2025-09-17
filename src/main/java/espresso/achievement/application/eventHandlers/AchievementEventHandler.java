package espresso.achievement.application.eventHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.events.AchievementCelebrationEvent;
import espresso.achievement.domain.events.AchievementEvent;
import espresso.achievement.infrastructure.integrations.AchievementEventPublisher;

@Component
public class AchievementEventHandler {

    @Autowired
    private AchievementEventPublisher achievementEventPublisher;

    @EventListener
    public void handleNewAchievementCreatedEvent(AchievementCelebrationEvent event) {
        System.out.println("EVENT HANDLED: " + event.toString());

        this.achievementEventPublisher.publishCelebrationCreatedEvent(event);
    }

    @EventListener
    public void handleNewAchievementCreatedEvent(AchievementEvent event) {
        System.out.println("EVENT HANDLED: " + event.toString());

        this.achievementEventPublisher.publishAchievementEvent(event);
    }
}
