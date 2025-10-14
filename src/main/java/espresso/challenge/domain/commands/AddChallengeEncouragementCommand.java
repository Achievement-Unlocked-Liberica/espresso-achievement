package espresso.challenge.domain.commands;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import espresso.common.domain.commands.CommonCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Command for adding an encouragement to an existing challenge.
 * Contains the necessary data for creating a new challenge encouragement.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddChallengeEncouragementCommand extends CommonCommand {

    /**
     * The 7-character alphanumeric key of the user giving the encouragement
     */
    @JsonIgnore
    @Schema(hidden = true)
    @NotBlank(message = "LOCALIZE: USER KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: USER KEY MUST BE EXACTLY 7 CHARACTERS")
    private String userKey;

    /**
     * The 7-character alphanumeric key of the challenge to encourage
     */
    @JsonIgnore
    @Schema(hidden = true)
    @NotBlank(message = "LOCALIZE: CHALLENGE KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: CHALLENGE KEY MUST BE EXACTLY 7 CHARACTERS")
    private String challengeKey;

    /**
     * The number of encouragements to give (between 1 and 9)
     */
    @NotNull(message = "LOCALIZE: ENCOURAGEMENT COUNT MUST BE PROVIDED")
    @Min(value = 1, message = "LOCALIZE: ENCOURAGEMENT COUNT MUST BE GREATER THAN ZERO")
    @Max(value = 9, message = "LOCALIZE: ENCOURAGEMENT COUNT MUST BE LESS THAN 10")
    private Integer count;

    /**
     * Validates the command data including parent validation.
     * 
     * @return Set of validation error messages, empty if valid
     */
    @Override
    public Set<String> validateCustom() {
        // This command uses only JSR-303 annotations for validation.
        // Any additional domain-specific checks may be implemented here in the future.
        return new HashSet<>();
    }
}
