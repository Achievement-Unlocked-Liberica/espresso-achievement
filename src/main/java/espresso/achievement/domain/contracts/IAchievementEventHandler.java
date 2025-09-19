package espresso.achievement.domain.contracts;

import org.springframework.context.event.EventListener;

import espresso.achievement.domain.events.AchievementCelebrationEvent;
import espresso.achievement.domain.events.AchievementCommentEvent;
import espresso.achievement.domain.events.AchievementEvent;

public interface IAchievementEventHandler {

    void handleEvent(AchievementEvent event);

    void handleEvent(AchievementCelebrationEvent event);

    void handleEvent(AchievementCommentEvent event);

}