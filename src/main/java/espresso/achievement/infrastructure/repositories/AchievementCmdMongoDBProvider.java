package espresso.achievement.infrastructure.repositories;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import espresso.achievement.domain.entities.Achievement;

public interface AchievementCmdMongoDBProvider extends MongoRepository<Achievement, UUID> {
    
    @Query("{ 'key' : ?0 }")
    Achievement findByKey(String key);
}
