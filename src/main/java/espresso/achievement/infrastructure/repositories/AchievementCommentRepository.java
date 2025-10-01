package espresso.achievement.infrastructure.repositories;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.contracts.IAchievementCommentRepository;
import espresso.achievement.domain.entities.AchievementComment;

/**
 * PostgreSQL implementation of the AchievementComment repository.
 * Handles persistence operations for achievement comments using JPA.
 */
@Primary
@Component
public class AchievementCommentRepository implements IAchievementCommentRepository {

    private final AchievementCommentPSQLProvider achievementCommentPSQLProvider;

    /**
     * Constructor for dependency injection.
     * 
     * @param achievementCommentPSQLProvider PostgreSQL data provider for achievement comment operations
     */
    public AchievementCommentRepository(AchievementCommentPSQLProvider achievementCommentPSQLProvider) {
        this.achievementCommentPSQLProvider = achievementCommentPSQLProvider;
    }

    /**
     * Saves an AchievementComment entity to the PostgreSQL database.
     * 
     * @param achievementComment The comment entity to save
     * @return The saved AchievementComment entity with populated ID
     * @throws IllegalArgumentException if the comment is null
     * @throws RuntimeException         if there's an error during persistence
     */
    @Override
    public AchievementComment save(AchievementComment achievementComment) {
        if (achievementComment == null) {
            throw new IllegalArgumentException("The achievement comment is null");
        }

        return this.achievementCommentPSQLProvider.save(achievementComment);
    }
}
