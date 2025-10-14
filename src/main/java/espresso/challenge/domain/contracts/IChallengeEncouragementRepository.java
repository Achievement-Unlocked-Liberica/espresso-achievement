package espresso.challenge.domain.contracts;

import espresso.challenge.domain.entities.ChallengeEncouragement;

/**
 * Contract for challenge encouragement repository operations.
 * Defines the methods needed to persist and emit encouragement data.
 */
public interface IChallengeEncouragementRepository {
    
    /**
     * Saves a challenge encouragement record.
     * 
     * @param encouragement The encouragement to save
     * @return The saved encouragement entity
     */
    ChallengeEncouragement save(ChallengeEncouragement encouragement);
}
