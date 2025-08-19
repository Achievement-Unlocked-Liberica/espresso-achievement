package espresso.achievement.infrastructure.repositories;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import espresso.achievement.domain.entities.Achievement;

@Repository
public interface AchievementPSQLProvider extends JpaRepository<Achievement, Long> {

    /**
     * Gets the achievement detail by key and projects it to the specified DTO type.
     * @param <T> The type of the DTO to project to (e.g., AchievementDtoLg.class)
     * @param type The DTO class to project to (e.g., AchievementDtoLg.class)
     * @param entityKey The key of the achievement to retrieve
     * @return A single achievement projected to the specified DTO type
     */
    @Query("SELECT a FROM Achievement a WHERE a.entityKey = :entityKey AND a.enabled = true")
    <T> T findAchievementByKey(Class<T> type, String entityKey);

    /**
     * Gets the latest achievements ordered by completion date (newest first)
     * @param <T> The type of the DTO to project to (e.g., AchievementDtoSm.class)
     * @param type The DTO class to project to (e.g., AchievementDtoSm.class)
     * @param limit Maximum number of results to return
     * @return List of achievements projected to the specified DTO type
     */
    @Query("SELECT a FROM Achievement a WHERE a.enabled = true ORDER BY a.registeredAt DESC")
    <T> List<T> findLatestAchievements(Class<T> type, Limit limit);

    /**
     * Gets the latest achievements ordered by completion date (newest first)
     * @param <T> The type of the DTO to project to (e.g., AchievementDtoSm.class)
     * @param type The DTO class to project to (e.g., AchievementDtoSm.class)
     * @param limit Maximum number of results to return
     * @param fromDate The date from which to retrieve achievements
     * @return List of achievements projected to the specified DTO type
     */
    @Query("SELECT a FROM Achievement a WHERE a.enabled = true AND a.registeredAt > :fromDate ORDER BY a.registeredAt DESC")
    <T> List<T> findLatestAchievements(Class<T> type, Limit limit, OffsetDateTime fromDate);

    /**
     * Updates an achievement in the database matching the id, achievementKey, and userKey.
     * This method leverages JPA's built-in save method which performs an update if the entity has an ID.
     * 
     * @param achievement The achievement entity to update
     * @return The updated achievement entity
     */
    default Achievement updateAchievement(Achievement achievement) {
        return save(achievement);
    }

    /**
     * Deletes an achievement and all its associated dependencies in the correct order to maintain referential integrity.
     * The deletion order is:
     * 1. Delete all comments associated with the achievement
     * 2. Delete all media files associated with the achievement
     * 3. Delete the achievement record itself
     * 
     * Uses database transactions to ensure atomicity of the entire deletion process.
     * Handles foreign key constraints properly to avoid constraint violations.
     * 
     * @param achievement The achievement entity to delete along with its dependencies
     * @throws RuntimeException if there's an error during any deletion step
     */
    @Modifying
    @Query(value = "DELETE FROM achievement_comments WHERE achievement_id = :#{#achievement.id}", nativeQuery = true)
    void deleteAchievementComments(Achievement achievement);

    @Modifying
    @Query(value = "DELETE FROM achievement_media WHERE achievement_id = :#{#achievement.id}", nativeQuery = true)
    void deleteAchievementMedia(Achievement achievement);

    /**
     * Orchestrates the cascading deletion of an achievement and all its dependencies.
     * This method ensures proper deletion order and transaction management.
     * 
     * @param achievement The achievement entity to delete
     */
    @Transactional
    default void deleteAchievementWithDependencies(Achievement achievement) {
        // Step 1: Delete all comments associated with the achievement
        deleteAchievementComments(achievement);
        
        // Step 2: Delete all media files associated with the achievement
        deleteAchievementMedia(achievement);
        
        // Step 3: Delete the achievement record itself
        delete(achievement);
    }

}