package espresso.achievement.domain.commands;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import espresso.achievement.domain.constants.AchievementConstants;
import espresso.common.domain.commands.CommonCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.*;

/**
 * Command for creating a new achievement.
 * Contains all necessary data for achievement creation including title, description, skills, and visibility.
 */
@Getter
@Setter
public class CreateAchivementCommand extends CommonCommand {

    /**
     * The 7-character alphanumeric key of the user creating the achievement.
     * This value is obtained from the JWT token and not from the request body.
     */
    @JsonIgnore
    @Schema(hidden = true)
    @NotBlank(message = "LOCALIZE:  A USER KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS")
    private String userKey;

    /**
     * The title or name of the achievement (maximum 200 characters).
     */
    @NotBlank(message = "LOCALIZE: A TITLE MUST BE PROVIDED")
    @Size(max = 200, message = "LOCALIZE: TITLE MUST NOT BE GREATER THAN 200 CHARACTERS")
    private String title;

    /**
     * A detailed description of the achievement (maximum 1000 characters).
     */
    @NotBlank(message = "LOCALIZE: A DESCRIPTION MUST BE PROVIDED")
    @Size(max = 1000, message = "LOCALIZE: TITLE MUST NOT BE GREATER THAN 1000 CHARACTERS")
    private String description;

    /**
     * The date when the achievement was completed. Must be today or in the past.
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "LOCALIZE: THE COMPLETED DATE CANNOT BE AFTER TODAY")
    private Date completedDate;

    /**
     * Array of skill abbreviations associated with this achievement.
     * Must contain between 1 and 7 valid skill abbreviations.
     */
    @Size(min = 1, max = 7, message = "LOCALIZE: AT LEAST ONE SKILL MUST BE PROVIDED")
    private String[] skills;

    /**
     * Whether the achievement should be publicly visible to other users.
     * Defaults to true if not specified.
     */
    private Boolean isPublic = true;

    /**
     * Performs custom validation beyond standard JSR-303 annotations.
     * Validates that all provided skills are within the allowed set.
     * 
     * @return Set of validation error messages, empty if valid
     */
    @Override
    public Set<String> validateCustom() {
        // Create a new mutable set to collect custom validation errors
        Set<String> errors = new HashSet<>();

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