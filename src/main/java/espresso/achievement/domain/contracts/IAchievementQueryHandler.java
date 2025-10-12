package espresso.achievement.domain.contracts;

import espresso.achievement.domain.queries.GetAchievementDetailQuery;
import espresso.achievement.domain.queries.GetLatestAchievementsQuery;
import espresso.achievement.domain.queries.GetMyAchievementsQuery;
import espresso.achievement.domain.queries.GetUserAchievementsQuery;
import espresso.common.domain.responses.HandlerResponse;

public interface IAchievementQueryHandler {

    /**
     * Handles the query to get the latest achievements
     * @param qry the query containing parameters for latest achievements
     * @return a HandlerResponse containing the achievement list
     */
    public HandlerResponse<Object> handle(GetLatestAchievementsQuery qry);

    /**
     * Handles the query to get detailed information about an achievement
     * @param qry the query containing the key of the achievement
     * @return a HandlerResponse containing the achievement detail read model
     */
    public HandlerResponse<Object> handle(GetAchievementDetailQuery qry);

    /**
     * Handles the query to get achievements for the authenticated user
     * @param qry the query containing parameters for user's achievements
     * @return a HandlerResponse containing the user's achievement list
     */
    public HandlerResponse<Object> handle(GetMyAchievementsQuery qry);

    /**
     * Handles the query to get achievements for a specific user
     * @param qry the query containing the userKey and parameters
     * @return a HandlerResponse containing the specified user's achievement list
     */
    public HandlerResponse<Object> handle(GetUserAchievementsQuery qry);

}
