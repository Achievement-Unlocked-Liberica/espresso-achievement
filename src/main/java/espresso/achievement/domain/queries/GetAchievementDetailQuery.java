package espresso.achievement.domain.queries;

import java.time.OffsetDateTime;

import espresso.common.domain.queries.CommonQuery;
import espresso.common.domain.queries.QuerySizeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Query for retrieving detailed information about a specific achievement.
 * Returns comprehensive achievement data including media, comments, and relationships.
 */
@Getter
@AllArgsConstructor
public class GetAchievementDetailQuery extends CommonQuery {

    /**
     * The size type indicating how much detail to include in the response (SM, MD, LG).
     */
    @NotNull(message = "LOCALIZE: THE SIZE MUST NOT BE NULL")
    private QuerySizeType size;

    /**
     * The 7-character alphanumeric key of the achievement to retrieve.
     */
    @NotNull(message = "LOCALIZE: THE KEY MUST NOT BE NULL")
    @Size(min = 7, max = 7, message = "LOCALIZE: THE KEY MUST BE 7 CHARACTERS")
    private String entityKey;
}