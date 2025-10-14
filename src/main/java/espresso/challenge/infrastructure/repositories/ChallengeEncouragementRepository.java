package espresso.challenge.infrastructure.repositories;

import org.springframework.stereotype.Repository;

import espresso.challenge.domain.contracts.IChallengeEncouragementRepository;
import espresso.challenge.domain.entities.ChallengeEncouragement;

/**
 * Repository implementation for challenge encouragement operations.
 * Delegates to data providers for persistence and message queue operations.
 */
@Repository
public class ChallengeEncouragementRepository implements IChallengeEncouragementRepository {

    private final ChallengeEncouragementPSQLProvider psqlProvider;

    /**
     * Constructor for dependency injection.
     * 
     * @param psqlProvider PostgreSQL data provider for challenge encouragement operations
     */
    public ChallengeEncouragementRepository(ChallengeEncouragementPSQLProvider psqlProvider) {
        this.psqlProvider = psqlProvider;
    }

    /**
     * Saves a challenge encouragement record using the PostgreSQL provider.
     * 
     * @param encouragement The encouragement to save
     * @return The saved encouragement entity
     */
    @Override
    public ChallengeEncouragement save(ChallengeEncouragement encouragement) {
        return psqlProvider.save(encouragement);
    }
}
