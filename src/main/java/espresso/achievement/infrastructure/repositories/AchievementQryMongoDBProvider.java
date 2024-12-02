package espresso.achievement.infrastructure.repositories;

import java.util.UUID;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.domain.readModels.AchievementSummaryReadModel;

public interface AchievementQryMongoDBProvider extends MongoRepository<Achievement, UUID> {
    
    @Query("{ 'key': ?0 }")
    List<AchievementDetailReadModel> findDetailByKey(String key);

    @Query("{ 'key': ?0 }")
    List<AchievementSummaryReadModel> findSummaryByKey(String key);

    @Query("{ 'userProfile.key': ?0 }")
    List<AchievementSummaryReadModel> findSummaryByUserKey(String key);
    
}