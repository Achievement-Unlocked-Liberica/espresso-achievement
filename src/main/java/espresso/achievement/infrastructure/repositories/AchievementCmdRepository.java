package espresso.achievement.infrastructure.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import espresso.achievement.domain.contracts.IAchievementCmdRepository;
import espresso.achievement.domain.entities.Achievement;

@Primary
@Component
public class AchievementCmdRepository implements IAchievementCmdRepository {

    @Autowired
    AchievementPSQLProvider achievementPSQLProvider;

    @Override
    public Achievement save(Achievement achievement) {

        try {
            if (achievement == null) {
                throw new IllegalArgumentException("The achievement is null");
            }

            Achievement entity = this.achievementPSQLProvider.save(achievement);

            return entity;

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }

    }
}
