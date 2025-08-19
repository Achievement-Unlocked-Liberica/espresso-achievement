package espresso.achievement.domain.commands;

import espresso.common.domain.commands.CommonCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;

/**
 * Command for disabling an existing achievement.
 * This command sets the achievement's enabled property to false, removing it from filters, searches, and visibility.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Command for disabling an achievement")
public class DisableAchievementCommand extends CommonCommand {

    /**
     * The 7-character alphanumeric key of the achievement to disable
     */
    @NotBlank(message = "LOCALIZE: ACHIEVEMENT KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS")
    @Schema(description = "The 7-character alphanumeric key of the achievement to disable", example = "ACHI001")
    private String achievementKey;

    /**
     * The 7-character alphanumeric key of the user who owns the achievement
     */
    @NotBlank(message = "LOCALIZE: A USER KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS")
    @Schema(description = "The 7-character alphanumeric key of the user who owns the achievement", example = "USER123")
    private String userKey;
}
