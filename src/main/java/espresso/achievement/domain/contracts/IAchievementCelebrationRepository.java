package espresso.achievement.domain.contracts;

import espresso.achievement.domain.entities.AchievementCelebration;

/**
 * Contract for achievement celebration repository operations.
 * Defines the methods needed to persist and emit celebration data.
 */
public interface IAchievementCelebrationRepository {
    
    /**
     * Saves an achievement celebration record.
     * 
     * @param celebration The celebration to save
     * @return The saved celebration entity
     */
    AchievementCelebration save(AchievementCelebration celebration);
    
    /**
     * Emits an achievement celebration to the message queue for processing.
     * 
     * @param celebration The celebration to emit to the queue
     */
    void emit(AchievementCelebration celebration);
}
