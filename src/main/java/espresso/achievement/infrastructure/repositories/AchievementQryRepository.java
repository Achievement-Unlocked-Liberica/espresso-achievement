package espresso.achievement.infrastructure.repositories;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.contracts.IAchievementQryRepository;
import espresso.achievement.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.domain.readModels.AchievementSummaryReadModel;

@Component
public class AchievementQryRepository implements IAchievementQryRepository {

    @Autowired
    AchievementPSQLProvider achievementPSQLProvider;

    @Override
    public AchievementDetailReadModel getAchievementDetailByKey(String key) {

        try {
            if (key == null) {
                throw new IllegalArgumentException("The key is null");
            }

            List<AchievementDetailReadModel> entities = new ArrayList<AchievementDetailReadModel>(); // this.achievementMongoDBProvider.findDetailByKey(key);

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

            List<AchievementSummaryReadModel> entities = new ArrayList<AchievementSummaryReadModel>(); // this.achievementMongoDBProvider.findSummaryByKey(key);

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

            List<AchievementSummaryReadModel> entities = new ArrayList<AchievementSummaryReadModel>(); // this.achievementMongoDBProvider.findSummaryByUserKey(userKey);

            return entities.isEmpty()
                    ? null
                    : entities;

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public <T> List<T> getLatestAchievements(Class<T> dtoType, Integer limit, OffsetDateTime fromDate) {
        try {

            if (limit == null || limit <= 0) {
                limit = 10; // Default limit
            }

            if (fromDate == null) {
                // Use the PSQL provider to get achievements with DTO projection
                return achievementPSQLProvider.findLatestAchievements(dtoType, Limit.of(limit));
            } else {
                // Use the PSQL provider to get achievements with DTO projection and fromDate
                // filter
                return achievementPSQLProvider.findLatestAchievements(dtoType, Limit.of(limit), fromDate);
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

}
