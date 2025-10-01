package espresso.achievement.infrastructure.repositories;

import org.springframework.stereotype.Repository;

import espresso.achievement.domain.contracts.IAchievementCelebrationRepository;
import espresso.achievement.domain.entities.AchievementCelebration;

/**
 * Repository implementation for achievement celebration operations.
 * Delegates to data providers for persistence and message queue operations.
 */
@Repository
public class AchievementCelebrationRepository implements IAchievementCelebrationRepository {

    private final AchievementCelebrationPSQLProvider psqlProvider;

    /**
     * Constructor for dependency injection.
     * 
     * @param psqlProvider PostgreSQL data provider for achievement celebration operations
     */
    public AchievementCelebrationRepository(AchievementCelebrationPSQLProvider psqlProvider) {
        this.psqlProvider = psqlProvider;
    }

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
