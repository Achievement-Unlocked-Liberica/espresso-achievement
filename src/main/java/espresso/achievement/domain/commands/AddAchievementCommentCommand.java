package espresso.achievement.domain.commands;

import java.util.HashSet;
import java.util.Set;

import espresso.common.domain.commands.CommonCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Command for adding a comment to an existing achievement.
 * Contains the necessary data for creating a new achievement comment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddAchievementCommentCommand extends CommonCommand {

    /**
     * The 7-character alphanumeric key of the achievement to comment on
     */
    @NotBlank(message = "LOCALIZE: ACHIEVEMENT KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS")
    private String achievementKey;

    /**
     * The 7-character alphanumeric key of the user posting the comment
     */
    @NotBlank(message = "LOCALIZE: USER KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: USER KEY MUST BE EXACTLY 7 CHARACTERS")
    private String userKey;

    /**
     * The text content of the comment
     */
    @NotBlank(message = "LOCALIZE: COMMENT TEXT MUST BE PROVIDED")
    @Size(max = 200, message = "LOCALIZE: COMMENT TEXT MUST NOT EXCEED 200 CHARACTERS")
    private String commentText;

    /**
     * Validates the command data including parent validation.
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

        // Additional custom validation could be added here if needed
        // For now, JSR-303 annotations handle all validation requirements

        return errors;
    }
}
