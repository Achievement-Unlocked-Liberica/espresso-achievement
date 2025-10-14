package espresso.challenge.domain.contracts;

import espresso.challenge.domain.queries.GetChallengeDetailQuery;
import espresso.challenge.domain.queries.GetLatestChallengesQuery;
import espresso.challenge.domain.queries.GetMyChallengesQuery;
import espresso.challenge.domain.queries.GetUserChallengesQuery;
import espresso.common.domain.responses.HandlerResponse;

public interface IChallengeQueryHandler {

    /**
     * Handles the query to get the latest challenges
     * @param qry the query containing parameters for latest challenges
     * @return a HandlerResponse containing the challenge list
     */
    public HandlerResponse<Object> handle(GetLatestChallengesQuery qry);

    /**
     * Handles the query to get detailed information about a challenge
     * @param qry the query containing the key of the challenge
     * @return a HandlerResponse containing the challenge detail read model
     */
    public HandlerResponse<Object> handle(GetChallengeDetailQuery qry);

    /**
     * Handles the query to get challenges for the authenticated user
     * @param qry the query containing parameters for user's challenges
     * @return a HandlerResponse containing the user's challenge list
     */
    public HandlerResponse<Object> handle(GetMyChallengesQuery qry);

    /**
     * Handles the query to get challenges for a specific user
     * @param qry the query containing the userKey and parameters
     * @return a HandlerResponse containing the specified user's challenge list
     */
    public HandlerResponse<Object> handle(GetUserChallengesQuery qry);
}
