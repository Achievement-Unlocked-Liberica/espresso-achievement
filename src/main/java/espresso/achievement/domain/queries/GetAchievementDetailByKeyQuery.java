package espresso.achievement.domain.queries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Query for retrieving detailed information about a specific achievement by its key.
 * Returns comprehensive achievement data including media, comments, and user information.
 */
@Getter
@AllArgsConstructor
public class GetAchievementDetailByKeyQuery {

    /**
     * The 7-character alphanumeric key of the achievement to retrieve.
     */
    @NotBlank
    @Size(min = 7, max = 7, message = "The key should be 7 characters")
    private final String key;
}
