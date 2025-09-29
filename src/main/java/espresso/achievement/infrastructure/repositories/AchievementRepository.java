package espresso.achievement.infrastructure.repositories;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.operational.exceptionPolicy.AchievementException;
import espresso.achievement.domain.operational.validationPolicy.AchievementValidator;

/**
 * Primary implementation of the Achievement repository interface.
 * Provides a unified interface for both command and query operations on Achievement entities.
 * Delegates actual data operations to specialized providers (PostgreSQL) while maintaining
 * proper validation and error handling at the repository level.
 */
@Primary
@Component
public class AchievementRepository implements IAchievementRepository {

    /**
     * Default page size for query operations, configurable via application properties.
     */
    @Value("${achievement.query.defaultPageSize}")
    private Integer queryDefaultPageSize;

    /**
     * PostgreSQL data provider for achievement entity operations.
     */
    @Autowired
    AchievementPSQLProvider achievementPSQLProvider;

    // Command operations
    @Override
    public Achievement save(Achievement achievement) {
        try {
            AchievementValidator.validateForPersistence(achievement);

            return this.achievementPSQLProvider.save(achievement);

        } catch (AchievementException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw AchievementException.creationFailed("Achievement with this key already exists");
        } catch (DataAccessException e) {
            throw AchievementException.creationFailed("Database error during achievement save operation");
        } catch (Exception e) {
            throw AchievementException.creationFailed("Unexpected error occurred while saving achievement");
        }
    }

    /**
     * Updates an existing achievement in the database.
     * Validates the achievement entity and forwards the update to the PostgreSQL provider.
     * 
     * @param achievement The achievement entity to update
     * @return The updated Achievement entity
     * @throws AchievementException if the achievement is null or update fails
     */
    @Override
    public Achievement update(Achievement achievement) {
        try {
            AchievementValidator.validateForUpdate(achievement);

            return this.achievementPSQLProvider.updateAchievement(achievement);

        } catch (AchievementException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (EmptyResultDataAccessException e) {
            throw AchievementException.notFound(achievement.getEntityKey());
        } catch (DataIntegrityViolationException e) {
            throw AchievementException.updateFailed(achievement.getEntityKey(), "Data integrity violation");
        } catch (DataAccessException e) {
            throw AchievementException.updateFailed(achievement.getEntityKey(), "Database error during update operation");
        } catch (Exception e) {
            throw AchievementException.updateFailed(
                achievement != null ? achievement.getEntityKey() : "unknown", 
                "Unexpected error occurred during update"
            );
        }
    }

    /**
     * Deletes an achievement and all its associated dependencies in the proper order.
     * The deletion order is: comments first, then media files, then the achievement record itself.
     * Uses database transactions to ensure atomicity of the entire deletion process.
     * 
     * @param achievement The achievement entity to delete along with its dependencies
     * @throws AchievementException if the achievement is null or deletion fails
     */
    @Override
    public void deleteWithDependencies(Achievement achievement) {
        try {
            AchievementValidator.validateForDeletion(achievement);

            // Forward to PSQL provider to delete the achievement and all associated data in proper dependency order
            this.achievementPSQLProvider.deleteAchievementWithDependencies(achievement);

        } catch (AchievementException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (EmptyResultDataAccessException e) {
            throw AchievementException.notFound(achievement.getEntityKey());
        } catch (DataIntegrityViolationException e) {
            throw AchievementException.updateFailed(achievement.getEntityKey(), "Cannot delete achievement due to foreign key constraints");
        } catch (DataAccessException e) {
            throw AchievementException.updateFailed(achievement.getEntityKey(), "Database error during deletion operation");
        } catch (Exception e) {
            throw AchievementException.updateFailed(
                achievement != null ? achievement.getEntityKey() : "unknown", 
                "Unexpected error occurred during deletion"
            );
        }
    }

    // Query operations
    @Override
    public <T> List<T> getLatestAchievements(Class<T> dtoType, Integer limit, OffsetDateTime fromDate) {
        try {
            AchievementValidator.validateDtoType(dtoType);
            limit = AchievementValidator.validateAndNormalizeLimit(limit);

            // If fromDate is null, get all latest achievements
            // If fromDate is provided, filter achievements from that date
            // This allows for pagination and filtering based on date
            return fromDate == null
                    ? achievementPSQLProvider.findLatestAchievements(dtoType, Limit.of(limit))
                    : achievementPSQLProvider.findLatestAchievements(dtoType, Limit.of(limit), fromDate);

        } catch (AchievementException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataAccessException e) {
            throw AchievementException.creationFailed("Database error while retrieving latest achievements");
        } catch (Exception e) {
            throw AchievementException.creationFailed("Unexpected error occurred while retrieving latest achievements");
        }
    }

    @Override
    public <T> T getAchievementByKey(Class<T> dtoType, String entityKey) {
        try {
            AchievementValidator.validateDtoType(dtoType);
            AchievementValidator.validateEntityKey(entityKey);

            T entity = achievementPSQLProvider.findAchievementByKey(dtoType, entityKey);
            
            AchievementValidator.validateQueryResult(entity, entityKey);
            
            return entity;

        } catch (AchievementException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataAccessException e) {
            throw AchievementException.creationFailed("Database error while retrieving achievement by key: " + entityKey);
        } catch (Exception e) {
            throw AchievementException.creationFailed("Unexpected error occurred while retrieving achievement by key: " + entityKey);
        }
    }
}
