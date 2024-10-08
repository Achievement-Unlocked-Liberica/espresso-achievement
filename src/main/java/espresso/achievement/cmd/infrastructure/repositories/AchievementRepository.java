package espresso.achievement.cmd.infrastructure.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import espresso.achievement.cmd.domain.contracts.IAchievementRepository;
import espresso.achievement.cmd.domain.entities.Achievement;

@Repository
public class AchievementRepository implements IAchievementRepository {

    @Autowired
    AchievementMongoDBProvider achievementMongoDBProvider;

    @Override
    public void save(Achievement achievement) {

        try {
            if (achievement == null) {
                throw new IllegalArgumentException("The achievement is null");
            }

            this.achievementMongoDBProvider.save(achievement);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

}
