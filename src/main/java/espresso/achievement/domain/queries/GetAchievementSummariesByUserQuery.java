package espresso.achievement.domain.queries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Query for retrieving summary information of all achievements for a specific user.
 * Returns a list of achievement summaries without detailed content.
 */
@Getter
@AllArgsConstructor
public class GetAchievementSummariesByUserQuery {
    
    /**
     * The 7-character alphanumeric key of the user whose achievements to retrieve.
     */
    @NotBlank
    @Size(min = 7, max = 7, message = "The key should be 7 characters")
    private final String userKey;
}
