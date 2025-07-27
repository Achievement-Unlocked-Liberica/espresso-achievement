package espresso.achievement.infrastructure.repositories;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.contracts.IAchievementQryRepository;

@Component
public class AchievementQryRepository implements IAchievementQryRepository {

    @Autowired
    AchievementPSQLProvider achievementPSQLProvider;

    @Override
    public <T> List<T> getLatestAchievements(Class<T> dtoType, Integer limit, OffsetDateTime fromDate) {

        List<T> entities;

        if (limit == null || limit <= 0) {
            limit = 10; // Default limit
        }

        // If fromDate is null, get all latest achievements
        // If fromDate is provided, filter achievements from that date
        // This allows for pagination and filtering based on date
        entities = fromDate == null
                ? achievementPSQLProvider.findLatestAchievements(dtoType, Limit.of(limit))
                : achievementPSQLProvider.findLatestAchievements(dtoType, Limit.of(limit), fromDate);

        return entities;
    }

    @Override
    public <T> T getAchievementByKey(Class<T> dtoType, String entityKey) {

        T entity;

        entity = achievementPSQLProvider.findAchievementByKey(dtoType, entityKey);

        return entity;
    }

}
