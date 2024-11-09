package espresso.achievement.cmd.application.eventHandlers;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import espresso.achievement.cmd.domain.events.NewAchievementCreated;

@Component
public class AchievementEventHandler {
    
    @EventListener
    public void handleNewAchievementCreatedEvent(NewAchievementCreated event) {
        System.out.println("EVENT HANDLED: " + event.toString());
    }
}
