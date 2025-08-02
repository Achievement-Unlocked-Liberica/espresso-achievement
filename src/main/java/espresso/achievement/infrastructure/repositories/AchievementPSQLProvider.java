package espresso.achievement.infrastructure.repositories;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
    @Query("SELECT a FROM Achievement a WHERE a.entityKey = :entityKey")
    <T> T findAchievementByKey(Class<T> type, String entityKey);

    /**
     * Gets the latest achievements ordered by completion date (newest first)
     * @param <T> The type of the DTO to project to (e.g., AchievementDtoSm.class)
     * @param type The DTO class to project to (e.g., AchievementDtoSm.class)
     * @param limit Maximum number of results to return
     * @return List of achievements projected to the specified DTO type
     */
    @Query("SELECT a FROM Achievement a ORDER BY a.registeredAt DESC")
    <T> List<T> findLatestAchievements(Class<T> type, Limit limit);

    /**
     * Gets the latest achievements ordered by completion date (newest first)
     * @param <T> The type of the DTO to project to (e.g., AchievementDtoSm.class)
     * @param type The DTO class to project to (e.g., AchievementDtoSm.class)
     * @param limit Maximum number of results to return
     * @param fromDate The date from which to retrieve achievements
     * @return List of achievements projected to the specified DTO type
     */
    @Query("SELECT a FROM Achievement a WHERE a.registeredAt > :fromDate ORDER BY a.registeredAt DESC")
    <T> List<T> findLatestAchievements(Class<T> type, Limit limit, OffsetDateTime fromDate);

}