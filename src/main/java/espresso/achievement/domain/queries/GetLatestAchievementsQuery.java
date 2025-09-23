package espresso.achievement.domain.queries;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

import espresso.common.domain.queries.CommonQuery;
import espresso.common.domain.queries.QuerySizeType;

/**
 * Query for retrieving the latest achievements from the system.
 * Can filter by date and limit the number of results returned.
 */
@Getter
@AllArgsConstructor
public class GetLatestAchievementsQuery extends CommonQuery {

    /**
     * The size type indicating how much detail to include in the response (SM, MD, LG).
     */
    @NotNull(message = "LOCALIZE: DTO SIZE MUST NOT BE NULL")
    private QuerySizeType size;
    
    /**
     * Optional filter to get achievements from this date onwards.
     * If null, no date filtering is applied.
     */
    private OffsetDateTime fromDate;
    
    /**
     * The maximum number of achievements to return.
     * Defaults to 10 if not specified.
     */
    private Integer limit = 10;
}
