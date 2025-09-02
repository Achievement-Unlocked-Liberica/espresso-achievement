package espresso.achievement.infrastructure.repositories;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import espresso.achievement.domain.entities.AchievementCelebration;

/**
 * PostgreSQL data provider for achievement celebration operations.
 * Currently configured to not persist to database as per requirements.
 */
@Service
public class AchievementCelebrationPSQLProvider {

    /**
     * Saves an achievement celebration record to PostgreSQL.
     * Currently returns the instance without persisting as per requirements.
     * 
     * @param celebration The celebration to save
     * @return The celebration instance (unchanged)
     */
    @Transactional
    public AchievementCelebration save(AchievementCelebration celebration) {
        // As per requirements, this method does not save to DB, just returns
        return celebration;
    }
}
