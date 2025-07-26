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

    @Query("SELECT a FROM Achievement a WHERE a.entityKey = :entityKey")
    <T> T findByKey(String entityKey, Class<T> type);

    /**
     * Gets the latest achievements ordered by completion date (newest first)
     * 
     * @param type  The DTO class to project to
     * @param limit Maximum number of results to return
     * @return List of achievements projected to the specified DTO type
     */
    @Query("SELECT a FROM Achievement a ORDER BY a.registeredAt DESC")
    <T> List<T> findLatestAchievements(Class<T> type, Limit limit);

    @Query("SELECT a FROM Achievement a WHERE a.registeredAt > :fromDate ORDER BY a.registeredAt DESC")
    <T> List<T> findLatestAchievements(Class<T> type, Limit limit, OffsetDateTime fromDate);

    // @Query("{ 'key': ?0 }")
    // List<AchievementSummaryReadModel> findSummaryByKey(String key);

    // @Query("{ 'userProfile.key': ?0 }")
    // List<AchievementSummaryReadModel> findSummaryByUserKey(String key);

}