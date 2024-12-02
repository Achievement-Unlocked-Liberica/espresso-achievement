package espresso.achievement.application.eventHandlers;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.events.NewAchievementCreated;

@Component
public class AchievementEventHandler {
    
    @EventListener
    public void handleNewAchievementCreatedEvent(NewAchievementCreated event) {
        System.out.println("EVENT HANDLED: " + event.toString());

        
    }
}
