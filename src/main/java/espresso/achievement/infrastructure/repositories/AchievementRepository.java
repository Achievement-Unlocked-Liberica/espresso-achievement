package espresso.achievement.infrastructure.repositories;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.entities.Achievement;

@Primary
@Component
public class AchievementRepository implements IAchievementRepository {

    @Autowired
    AchievementPSQLProvider achievementPSQLProvider;

    // Command operations (from AchievementCmdRepository)
    @Override
    public Achievement save(Achievement achievement) {
        try {
            if (achievement == null) {
                throw new IllegalArgumentException("The achievement is null");
            }

            Achievement entity = this.achievementPSQLProvider.save(achievement);

            return entity;

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    /**
     * Updates an existing achievement in the database.
     * Validates the achievement entity and forwards the update to the PostgreSQL provider.
     * 
     * @param achievement The achievement entity to update
     * @return The updated Achievement entity
     * @throws IllegalArgumentException if the achievement is null
     * @throws RuntimeException if there's an error during the update operation
     */
    @Override
    public Achievement update(Achievement achievement) {
        // Use the PSQL provider's update method (leverages JPA save for updates)
        Achievement updatedEntity = this.achievementPSQLProvider.updateAchievement(achievement);

        return updatedEntity;
    }

    /**
     * Deletes an achievement and all its associated dependencies in the proper order.
     * The deletion order is: comments first, then media files, then the achievement record itself.
     * Uses database transactions to ensure atomicity of the entire deletion process.
     * 
     * @param achievement The achievement entity to delete along with its dependencies
     * @throws IllegalArgumentException if the achievement is null
     * @throws RuntimeException if there's an error during the deletion process
     */
    @Override
    public void deleteWithDependencies(Achievement achievement) {
        try {
            if (achievement == null) {
                throw new IllegalArgumentException("The achievement is null");
            }

            // Forward to PSQL provider to delete the achievement and all associated data in proper dependency order
            this.achievementPSQLProvider.deleteAchievementWithDependencies(achievement);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Query operations (from AchievementQryRepository)
    @Override
    public <T> List<T> getLatestAchievements(Class<T> dtoType, Integer limit, OffsetDateTime fromDate) {
        List<T> entities;

        if (limit == null || limit <= 0) {
            limit = 10; // Default limit
        }

        // If fromDate is null, get all latest achievements
        // If fromDate is provided, filter achievements from that date
        // This allows for pagination and filtering based on date
        entities = fromDate == null
                ? achievementPSQLProvider.findLatestAchievements(dtoType, Limit.of(limit))
                : achievementPSQLProvider.findLatestAchievements(dtoType, Limit.of(limit), fromDate);

        return entities;
    }

    @Override
    public <T> T getAchievementByKey(Class<T> dtoType, String entityKey) {
        T entity;

        entity = achievementPSQLProvider.findAchievementByKey(dtoType, entityKey);

        return entity;
    }
}
