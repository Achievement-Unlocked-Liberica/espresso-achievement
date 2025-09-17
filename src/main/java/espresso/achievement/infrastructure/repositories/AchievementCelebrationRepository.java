package espresso.achievement.infrastructure.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import espresso.achievement.domain.contracts.IAchievementCelebrationRepository;
import espresso.achievement.domain.entities.AchievementCelebration;

/**
 * Repository implementation for achievement celebration operations.
 * Delegates to data providers for persistence and message queue operations.
 */
@Repository
public class AchievementCelebrationRepository implements IAchievementCelebrationRepository {

    @Autowired
    private AchievementCelebrationPSQLProvider psqlProvider;

    /**
     * Saves an achievement celebration record using the PostgreSQL provider.
     * 
     * @param celebration The celebration to save
     * @return The saved celebration entity
     */
    @Override
    public AchievementCelebration save(AchievementCelebration celebration) {
        return psqlProvider.save(celebration);
    }
}
