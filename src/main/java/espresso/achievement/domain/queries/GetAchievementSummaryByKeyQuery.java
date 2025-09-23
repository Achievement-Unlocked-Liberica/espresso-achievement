package espresso.achievement.domain.queries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Query for retrieving summary information about a specific achievement by its key.
 * Returns basic achievement data without detailed content like comments or media.
 */
@Getter
@AllArgsConstructor
public class GetAchievementSummaryByKeyQuery {

    /**
     * The 7-character alphanumeric key of the achievement to retrieve.
     */
    @NotBlank
    @Size(min = 7, max = 7, message = "The key should be 7 characters")
    private final String key;
}
