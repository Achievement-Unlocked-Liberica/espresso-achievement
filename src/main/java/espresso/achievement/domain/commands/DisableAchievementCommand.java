package espresso.achievement.domain.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;

import espresso.common.domain.commands.CommonCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;

/**
 * Command for disabling an existing achievement.
 * This command sets the achievement's enabled property to false, removing it
 * from filters, searches, and visibility.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Command for disabling an achievement")
public class DisableAchievementCommand extends CommonCommand {

    /**
     * The 7-character alphanumeric key of the user who owns the achievement
     */
    @JsonIgnore
    @Schema(hidden = true)
    @NotBlank(message = "LOCALIZE: A USER KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS")
    private String userKey;

    /**
     * The 7-character alphanumeric key of the achievement to disable
     */
    @JsonIgnore
    @Schema(hidden = true)
    @NotBlank(message = "LOCALIZE: ACHIEVEMENT KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS")
    private String achievementKey;
}
