package espresso.challenge.domain.contracts;

import espresso.challenge.domain.entities.ChallengeComment;

/**
 * Repository interface for ChallengeComment entity operations.
 * Provides persistence operations for challenge comments.
 */
public interface IChallengeCommentRepository {
    
    /**
     * Saves a challenge comment to the repository.
     * 
     * @param comment The challenge comment to save
     * @return The saved ChallengeComment entity
     */
    ChallengeComment save(ChallengeComment comment);
}
