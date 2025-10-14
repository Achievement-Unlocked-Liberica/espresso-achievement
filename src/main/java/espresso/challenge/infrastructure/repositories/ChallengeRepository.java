package espresso.challenge.infrastructure.repositories;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import espresso.challenge.domain.contracts.IChallengeRepository;
import espresso.challenge.domain.entities.Challenge;
import espresso.challenge.domain.operational.exceptionPolicy.ChallengeException;
import espresso.challenge.domain.operational.validationPolicy.ChallengeValidator;

/**
 * Primary implementation of the Challenge repository interface.
 * Provides a unified interface for both command and query operations on Challenge entities.
 * Delegates actual data operations to specialized providers (PostgreSQL) while maintaining
 * proper validation and error handling at the repository level.
 */
@Primary
@Component
public class ChallengeRepository implements IChallengeRepository {

    private final ChallengePSQLProvider challengePSQLProvider;

    /**
     * Constructor for dependency injection.
     * 
     * @param challengePSQLProvider PostgreSQL data provider for challenge entity operations
     */
    public ChallengeRepository(ChallengePSQLProvider challengePSQLProvider) {
        this.challengePSQLProvider = challengePSQLProvider;
    }

    // Command operations
    @Override
    public Challenge save(Challenge challenge) {
        try {
            ChallengeValidator.validateForPersistence(challenge);

            return this.challengePSQLProvider.save(challenge);

        } catch (ChallengeException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw ChallengeException.creationFailed("Challenge with this key already exists");
        } catch (DataAccessException e) {
            throw ChallengeException.creationFailed("Database error during challenge save operation");
        } catch (Exception e) {
            throw ChallengeException.creationFailed("Unexpected error occurred while saving challenge");
        }
    }

    /**
     * Updates an existing challenge in the database.
     * Validates the challenge entity and forwards the update to the PostgreSQL provider.
     * 
     * @param challenge The challenge entity to update
     * @return The updated Challenge entity
     * @throws ChallengeException if the challenge is null or update fails
     */
    @Override
    public Challenge update(Challenge challenge) {
        try {
            ChallengeValidator.validateForUpdate(challenge);

            return this.challengePSQLProvider.updateChallenge(challenge);

        } catch (ChallengeException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (EmptyResultDataAccessException e) {
            throw ChallengeException.notFound(challenge.getEntityKey());
        } catch (DataIntegrityViolationException e) {
            throw ChallengeException.updateFailed(challenge.getEntityKey(), "Data integrity violation");
        } catch (DataAccessException e) {
            throw ChallengeException.updateFailed(challenge.getEntityKey(), "Database error during update operation");
        } catch (Exception e) {
            throw ChallengeException.updateFailed(
                challenge != null ? challenge.getEntityKey() : "unknown", 
                "Unexpected error occurred during update"
            );
        }
    }

    /**
     * Deletes a challenge and all its associated dependencies in the proper order.
     * Uses database transactions to ensure atomicity of the entire deletion process.
     * 
     * @param challenge The challenge entity to delete along with its dependencies
     * @throws ChallengeException if the challenge is null or deletion fails
     */
    @Override
    public void deleteWithDependencies(Challenge challenge) {
        try {
            if (challenge == null) {
                throw ChallengeException.invalidChallenge("Challenge cannot be null for deletion");
            }

            // Forward to PSQL provider to delete the challenge and all associated data in proper dependency order
            this.challengePSQLProvider.deleteChallengeWithDependencies(challenge);

        } catch (ChallengeException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (EmptyResultDataAccessException e) {
            String key = (challenge != null && challenge.getEntityKey() != null) ? challenge.getEntityKey() : "unknown";
            throw ChallengeException.notFound(key);
        } catch (DataIntegrityViolationException e) {
            String key = (challenge != null && challenge.getEntityKey() != null) ? challenge.getEntityKey() : "unknown";
            throw ChallengeException.updateFailed(key, "Cannot delete challenge due to foreign key constraints");
        } catch (DataAccessException e) {
            String key = (challenge != null && challenge.getEntityKey() != null) ? challenge.getEntityKey() : "unknown";
            throw ChallengeException.updateFailed(key, "Database error during deletion operation");
        } catch (Exception e) {
            throw ChallengeException.updateFailed(
                challenge != null ? challenge.getEntityKey() : "unknown", 
                "Unexpected error occurred during deletion"
            );
        }
    }

    @Override
    public <T> T getChallengeByKey(Class<T> dtoType, String entityKey) {
        try {
            return challengePSQLProvider.findChallengeByKey(dtoType, entityKey);
        } catch (Exception e) {
            // Log and return null on error to match achievement pattern
            return null;
        }
    }

    @Override
    public <T> List<T> getLatestChallenges(Class<T> dtoType, Integer limit, OffsetDateTime fromDate) {
        try {
            // Normalize limit to 10 if not provided or invalid
            if (limit == null || limit <= 0) {
                limit = 10;
            }

            // If fromDate is null, get all latest challenges
            // If fromDate is provided, filter challenges from that date
            return fromDate == null
                    ? challengePSQLProvider.findLatestChallenges(dtoType, org.springframework.data.domain.Limit.of(limit))
                    : challengePSQLProvider.findLatestChallenges(dtoType, org.springframework.data.domain.Limit.of(limit), fromDate);

        } catch (Exception e) {
            // Log and return null on error to match achievement pattern
            return null;
        }
    }

    @Override
    public <T> List<T> getChallengesByUserKey(Class<T> dtoType, String userKey, Integer limit, OffsetDateTime fromDate) {
        try {
            // Normalize limit to 10 if not provided or invalid
            if (limit == null || limit <= 0) {
                limit = 10;
            }

            // Get challenges for the specified user, filtered by date if provided
            return fromDate == null
                    ? challengePSQLProvider.findChallengesByUserKey(dtoType, userKey, org.springframework.data.domain.Limit.of(limit))
                    : challengePSQLProvider.findChallengesByUserKey(dtoType, userKey, org.springframework.data.domain.Limit.of(limit), fromDate);

        } catch (Exception e) {
            // Log and return null on error to match achievement pattern
            return null;
        }
    }
}
