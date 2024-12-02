package espresso.achievement.infrastructure.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import espresso.achievement.domain.contracts.IAchievementCmdRepository;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.KeyGenerator;
import espresso.achievement.domain.entities.PreMedia;

@Repository
public class AchievementCmdRepository implements IAchievementCmdRepository {

    @Autowired
    AchievementCmdMongoDBProvider achievementMongoDBProvider;

    @Override
    public Achievement save(Achievement achievement) {

        try {
            if (achievement == null) {
                throw new IllegalArgumentException("The achievement is null");
            }

            Achievement entity = this.achievementMongoDBProvider.save(achievement);

            return entity;

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }

    }

}
