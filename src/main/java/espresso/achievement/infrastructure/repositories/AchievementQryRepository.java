package espresso.achievement.infrastructure.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import espresso.achievement.domain.contracts.IAchievementQryRepository;
import espresso.achievement.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.domain.readModels.AchievementSummaryReadModel;

@Repository
public class AchievementQryRepository implements IAchievementQryRepository {

    @Autowired
    AchievementQryMongoDBProvider achievementMongoDBProvider;

    @Override
    public AchievementDetailReadModel getAchievementDetailByKey(String key) {

        try {
            if (key == null) {
                throw new IllegalArgumentException("The key is null");
            }

            List<AchievementDetailReadModel> entities = this.achievementMongoDBProvider.findDetailByKey(key);

            return entities.isEmpty()
                    ? null
                    : entities.getFirst();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public AchievementSummaryReadModel getAchievementSummaryByKey(String key) {
        try {
            if (key == null) {
                throw new IllegalArgumentException("The key is null");
            }

            List<AchievementSummaryReadModel> entities = this.achievementMongoDBProvider.findSummaryByKey(key);

            return entities.isEmpty()
                    ? null
                    : entities.getFirst();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<AchievementSummaryReadModel> getAchievementSummariesByUserKey(String userKey) {
        try {
            if (userKey == null) {
                throw new IllegalArgumentException("The key is null");
            }

            List<AchievementSummaryReadModel> entities = this.achievementMongoDBProvider.findSummaryByUserKey(userKey);

            return entities.isEmpty()
                    ? null
                    : entities;

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

}
