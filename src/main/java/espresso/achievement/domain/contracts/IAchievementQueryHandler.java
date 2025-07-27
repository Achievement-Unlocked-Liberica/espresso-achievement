package espresso.achievement.domain.contracts;

import espresso.achievement.domain.queries.GetAchievementDetailQuery;
import espresso.achievement.domain.queries.GetLatestAchievementsQuery;
import espresso.common.domain.responses.HandlerResponse;

public interface IAchievementQueryHandler {

    /**
     * Handles the query to get detailed information about an achievement
     * @param qry the query containing the key of the achievement
     * @return a HandlerResponse containing the achievement detail read model
     */
    public HandlerResponse<Object> handle(GetLatestAchievementsQuery qry);

    /**
     * Handles the query to get a summary of an achievement by its key
     * @param qry the query containing the key of the achievement
     * @return a HandlerResponse containing the achievement summary read model
     */
    public HandlerResponse<Object> handle(GetAchievementDetailQuery qry);

}
