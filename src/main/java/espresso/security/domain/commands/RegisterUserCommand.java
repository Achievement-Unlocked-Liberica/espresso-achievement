package espresso.security.domain.commands;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import espresso.common.domain.commands.CommonCommand;

/**
 * Command for registering a new user in the security domain.
 * Encapsulates user registration data including username, password, and email.
 * Enforces comprehensive validation rules for secure user account creation.
 * Extends CommonCommand to inherit standard command behavior and validation.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class RegisterUserCommand extends CommonCommand {

    /**
     * The username for the new user account.
     * Must be unique within the system and between 5-50 characters in length.
     * Serves as the primary login identifier for the user.
     */
    @NotBlank(message = "LOCALIZE: USERNAME MUST NOT BE BLANK")
    @Size(min = 5, max = 50, message = "LOCALIZE: USERNAME MUST BE BETWEEN 5 AND 50 CHARACTERS")
    private String username;

    /**
     * The password for the new user account.
     * Must meet strict security requirements including minimum length,
     * mixed case letters, numbers, and special characters for enhanced security.
     * Between 8-100 characters with complexity requirements enforced.
     */
    @NotBlank(message = "LOCALIZE: PASSWORD MUST NOT BE BLANK")
    @Size(min = 8, max = 100, message = "LOCALIZE: PASSWORD MUST BE BETWEEN 8 AND 100 CHARACTERS")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "LOCALIZE: PASSWORD MUST CONTAIN AT LEAST ONE LOWERCASE LETTER, ONE UPPERCASE LETTER, ONE NUMBER, AND ONE SPECIAL CHARACTER (@$!%*?&)"
    )
    private String password;

    /**
     * The email address for the new user account.
     * Must be a valid email format and unique within the system.
     * Used for account verification, password recovery, and notifications.
     */
    @NotBlank(message = "LOCALIZE: EMAIL MUST NOT BE BLANK")
    @Email(message = "LOCALIZE: EMAIL MUST BE A VALID EMAIL ADDRESS")
    private String email;
}
