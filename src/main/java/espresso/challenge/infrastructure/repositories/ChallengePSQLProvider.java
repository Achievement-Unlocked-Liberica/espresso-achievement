package espresso.challenge.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import espresso.challenge.domain.entities.Challenge;

@Repository
public interface ChallengePSQLProvider extends JpaRepository<Challenge, Long> {

    /**
     * Gets the challenge detail by key and projects it to the specified DTO type.
     * @param <T> The type of the DTO to project to
     * @param type The DTO class to project to
     * @param entityKey The key of the challenge to retrieve
     * @return A single challenge projected to the specified DTO type
     */
    @Query("SELECT c FROM Challenge c WHERE c.entityKey = :entityKey AND c.enabled = true")
    <T> T findChallengeByKey(Class<T> type, String entityKey);

    /**
     * Updates a challenge in the database matching the id, challengeKey, and userKey.
     * This method leverages JPA's built-in save method which performs an update if the entity has an ID.
     * 
     * @param challenge The challenge entity to update
     * @return The updated challenge entity
     */
    default Challenge updateChallenge(Challenge challenge) {
        return save(challenge);
    }

    /**
     * Orchestrates the cascading deletion of a challenge and all its dependencies.
     * This method ensures proper deletion order and transaction management.
     * 
     * @param challenge The challenge entity to delete
     */
    @Transactional
    default void deleteChallengeWithDependencies(Challenge challenge) {
        // For now, just delete the challenge
        // Future: Add deletion of comments, media, etc. when those are implemented
        delete(challenge);
    }
}
