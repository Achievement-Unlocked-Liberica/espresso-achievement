package espresso.achievement.domain.queries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAchievementDetailByKeyQuery {

    @NotBlank
    @Size(min = 7, max = 7, message = "The key should be 7 characters")
    private final String key;
}
