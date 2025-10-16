package espresso.challenge.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import espresso.challenge.domain.contracts.IChallengeCommentRepository;
import espresso.challenge.domain.entities.ChallengeComment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA implementation of the IChallengeCommentRepository interface.
 * Provides persistence operations for ChallengeComment entities using JPA EntityManager.
 */
@Repository
@Slf4j
public class ChallengeCommentRepository implements IChallengeCommentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves a challenge comment using JPA persist/merge operations.
     * 
     * @param comment The challenge comment to save
     * @return The persisted ChallengeComment entity
     */
    @Override
    public ChallengeComment save(ChallengeComment comment) {
        log.debug("Saving challenge comment for challengeId={}, userId={}", 
                  comment.getChallenge() != null ? comment.getChallenge().getId() : null,
                  comment.getUser() != null ? comment.getUser().getId() : null);
        
        if (comment.getId() == null) {
            entityManager.persist(comment);
            return comment;
        } else {
            return entityManager.merge(comment);
        }
    }
}
