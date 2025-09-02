package espresso.achievement.domain.events;

import espresso.common.domain.events.CommonEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Domain event raised when a celebration is added to an achievement.
 * This event captures the celebration action for downstream processing.
 */
@Getter
@Setter
@Builder
public class AchievementCelebrationAddedEvent extends CommonEvent {
    
    /**
     * The number of celebrations given
     */
    private Integer count;
    
    /**
     * The 7-character alphanumeric key of the achievement being celebrated
     */
    private String achievementKey;
    
    /**
     * The 7-character alphanumeric key of the user giving the celebration
     */
    private String userKey;
}
