package espresso.achievement.qry.infrastructure.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import espresso.achievement.qry.domain.contracts.IAchievementQryRepository;
import espresso.achievement.qry.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.qry.domain.readModels.AchievementSummaryReadModel;

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

}
