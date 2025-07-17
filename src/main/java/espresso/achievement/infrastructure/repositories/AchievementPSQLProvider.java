package espresso.achievement.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import espresso.achievement.domain.entities.Achievement;

@Repository
public interface AchievementPSQLProvider extends JpaRepository<Achievement, Long> {

    @Query("SELECT a FROM Achievement a WHERE a.entityKey = :entityKey")
    <T> T findByKey(String entityKey, Class<T> type);

    // @Query("{ 'key': ?0 }")
    // List<AchievementSummaryReadModel> findSummaryByKey(String key);

    // @Query("{ 'userProfile.key': ?0 }")
    // List<AchievementSummaryReadModel> findSummaryByUserKey(String key);

}