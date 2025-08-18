package espresso.achievement.domain.commands;

import espresso.achievement.domain.constants.AchievementConstants;
import espresso.common.domain.commands.CommonCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.*;

/**
 * Command for updating an existing achievement.
 * Contains the necessary data for updating achievement title, description, skills, and visibility.
 */
@Getter
@Setter
@Schema(description = "command for updating an achievement")
public class UpdateAchievementCommand extends CommonCommand {

    /**
     * The 7-character alphanumeric key of the achievement to update
     */
    @NotBlank(message = "LOCALIZE: ACHIEVEMENT KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS")
    private String achievementKey;

    /**
     * The 7-character alphanumeric key of the user who owns the achievement
     */
    @NotBlank(message = "LOCALIZE: A USER KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS")
    private String userKey;

    /**
     * The updated title of the achievement
     */
    @NotBlank(message = "LOCALIZE: A TITLE MUST BE PROVIDED")
    @Size(max = 200, message = "LOCALIZE: TITLE MUST NOT BE GREATER THAN 200 CHARACTERS")
    private String title;

    /**
     * The updated description of the achievement
     */
    @NotBlank(message = "LOCALIZE: A DESCRIPTION MUST BE PROVIDED")
    @Size(max = 1000, message = "LOCALIZE: DESCRIPTION MUST NOT BE GREATER THAN 1000 CHARACTERS")
    private String description;

    /**
     * Array of skill abbreviations associated with the achievement
     */
    @Size(min = 1, max = 7, message = "LOCALIZE: AT LEAST ONE SKILL MUST BE PROVIDED")
    private String[] skills;

    /**
     * Whether the achievement is publicly visible
     */
    private Boolean isPublic = true;

    /**
     * Validates the command data including parent validation and custom skill validation.
     * 
     * @return Set of validation error messages, empty if valid
     */
    @Override
    public Set<String> validate() {
        // Call parent validation first to get standard JSR-303 validation errors
        Set<String> parentErrors = super.validate();

        // Create a new mutable set to avoid UnsupportedOperationException
        Set<String> errors = new HashSet<>();
        if (parentErrors != null) {
            errors.addAll(parentErrors);
        }

        // Validate skills if present
        if (skills != null && skills.length > 0) {
            for (int i = 0; i < skills.length; i++) {
                String skill = skills[i];
                if (skill != null && !skill.trim().isEmpty()) {
                    String normalizedSkill = skill.trim().toLowerCase();
                    if (!AchievementConstants.ALLOWED_SKILLS.contains(normalizedSkill)) {
                        errors.add("skills[" + i + "]:" + String.format(AchievementConstants.ERROR_INVALID_SKILL, skill));
                    }
                }
            }
        }

        return errors;
    }
}
