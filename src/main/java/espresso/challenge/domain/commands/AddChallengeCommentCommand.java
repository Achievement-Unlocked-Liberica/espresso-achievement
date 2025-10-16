package espresso.challenge.domain.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;

import espresso.common.domain.commands.CommonCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Command to add a comment to a challenge.
 * Requires authentication - userKey is extracted from JWT token.
 * The challengeKey is extracted from the URL path parameter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Command to add a comment to a challenge")
public class AddChallengeCommentCommand extends CommonCommand {

    /**
     * Key of the authenticated user posting the comment.
     * Hidden from JSON - extracted from JWT token by controller.
     */
    @JsonIgnore
    @Schema(hidden = true)
    private String userKey;

    /**
     * Key of the challenge to comment on.
     * Hidden from JSON - extracted from URL path parameter by controller.
     */
    @JsonIgnore
    @Schema(hidden = true)
    private String challengeKey;

    /**
     * The text content of the comment.
     * Must not be blank and cannot exceed 200 characters.
     */
    @NotBlank(message = "Comment text is required")
    @Size(max = 200, message = "Comment text cannot exceed 200 characters")
    @Schema(
        description = "The text content of the comment",
        example = "This challenge really helped me improve my skills!",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 200
    )
    private String commentText;

    /**
     * Sets the user key (typically from JWT authentication).
     * 
     * @param userKey The authenticated user's key
     * @return This command for method chaining
     */
    public AddChallengeCommentCommand withUserKey(String userKey) {
        this.userKey = userKey;
        return this;
    }

    /**
     * Sets the challenge key (typically from URL path parameter).
     * 
     * @param challengeKey The challenge's key
     * @return This command for method chaining
     */
    public AddChallengeCommentCommand withChallengeKey(String challengeKey) {
        this.challengeKey = challengeKey;
        return this;
    }
}
