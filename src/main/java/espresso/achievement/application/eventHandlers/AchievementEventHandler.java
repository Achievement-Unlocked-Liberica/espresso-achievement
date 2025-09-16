package espresso.achievement.application.eventHandlers;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.events.AchievementCelebrationAddedEvent;
import espresso.achievement.domain.events.NewAchievementCreated;

@Component
public class AchievementEventHandler {
    
    @EventListener
    public void handleNewAchievementCreatedEvent(AchievementCelebrationAddedEvent event) {
        System.out.println("EVENT HANDLED: " + event.toString());

        
    }
}
