package espresso.achievement.domain.contracts;

import java.time.OffsetDateTime;
import java.util.List;

import espresso.achievement.domain.entities.Achievement;

public interface IAchievementRepository {

    // Command operations (from IAchievementCmdRepository)
    Achievement save(Achievement achievement);
    
    /**
     * Updates an existing achievement in the repository.
     * 
     * @param achievement The achievement entity to update
     * @return The updated Achievement entity
     */
    Achievement update(Achievement achievement);
    
    /**
     * Deletes an achievement and all its associated dependencies in the proper order.
     * The deletion order is: comments first, then media files, then the achievement record itself.
     * Uses database transactions to ensure atomicity of the entire deletion process.
     * 
     * @param achievement The achievement entity to delete along with its dependencies
     * @throws IllegalArgumentException if the achievement is null
     * @throws RuntimeException if there's an error during the deletion process
     */
    void deleteWithDependencies(Achievement achievement);

    // Query operations (from IAchievementQryRepository)
    /**
     * Gets the latest achievements ordered by completion date (newest first)
     * @param <T> The type of the DTO to project to (e.g., AchievementDtoSm.class)
     * @param dtoType The DTO class to project to (e.g., AchievementDtoSm.class)
     * @param limit Maximum number of results to return
     * @return List of achievements projected to the specified DTO type
     */
    <T> List<T> getLatestAchievements(Class<T> dtoType, Integer limit, OffsetDateTime fromDate);

    /**
     * Gets the achievement detail by key and projects it to the specified DTO type.
     * @param <T> The type of the DTO to project to (e.g., AchievementDtoLg.class)
     * @param dtoType The DTO class to project to (e.g., AchievementDtoLg.class)
     * @param entityKey The key of the achievement to retrieve
     * @return
     */
    <T> T getAchievementByKey(Class<T> dtoType, String entityKey);
}
