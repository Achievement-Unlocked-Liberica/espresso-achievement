package espresso.achievement.cmd.infrastructure.repositories;

import java.util.UUID;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import espresso.achievement.cmd.domain.entities.Achievement;

public interface AchievementMongoDBProvider extends MongoRepository<Achievement, UUID> {
    
    @Query("{ 'key' : ?0 }")
    Achievement findByKey(String key);
}
