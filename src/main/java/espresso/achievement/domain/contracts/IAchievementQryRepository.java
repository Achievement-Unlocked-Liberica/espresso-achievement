package espresso.achievement.domain.contracts;

import java.time.OffsetDateTime;
import java.util.List;

public interface IAchievementQryRepository {
    
    /**
     * Gets the latest achievements ordered by completion date (newest first)
     * @param <T> The type of the DTO to project to (e.g., AchievementDtoSm.class)
     * @param dtoType The DTO class to project to (e.g., AchievementDtoSm.class)
     * @param limit Maximum number of results to return
     * @return List of achievements projected to the specified DTO type
     */
    <T> List<T> getLatestAchievements(Class<T> dtoType, Integer limit, OffsetDateTime fromDate);


    /**
     * Gets the achievement detail by key and projects it to the specified DTO type.
     * @param <T> The type of the DTO to project to (e.g., AchievementDtoLg.class)
     * @param dtoType The DTO class to project to (e.g., AchievementDtoLg.class)
     * @param entityKey The key of the achievement to retrieve
     * @return
     */
    <T> T getAchievementByKey(Class<T> dtoType, String entityKey);
}
