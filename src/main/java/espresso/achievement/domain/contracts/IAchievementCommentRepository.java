package espresso.achievement.domain.contracts;

import espresso.achievement.domain.entities.AchievementComment;

/**
 * Repository interface for AchievementComment entity operations.
 * Defines the contract for persisting and retrieving achievement comments.
 */
public interface IAchievementCommentRepository {

    /**
     * Saves an AchievementComment entity to the data store.
     * 
     * @param achievementComment The comment entity to save
     * @return The saved AchievementComment entity with populated ID and timestamps
     */
    AchievementComment save(AchievementComment achievementComment);
}
