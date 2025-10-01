package espresso.achievement.domain.queries;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

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
    private Integer limit;

    /**
     * Performs custom validation specific to this query type.
     * Validates that fromDate, if provided, is not in the future.
     * 
     * @return Set of validation error messages, empty if valid
     */
    @Override
    public Set<String> validateCustom() {
        Set<String> errors = new HashSet<>();

        // Verify that fromDate is not null and is not after now (UTC)
        if (fromDate != null && fromDate.isAfter(OffsetDateTime.now(ZoneOffset.UTC))) {
            errors.add("fromDate must not be in the future");
        }

        return errors;
    }
}
