package espresso.user.domain.commands;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import espresso.common.domain.commands.CommonCommand;

/**
 * Command for adding/registering a new user to the system.
 * Contains all necessary information to create a complete user profile
 * including authentication credentials and personal information.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class AddUserCommand extends CommonCommand{

    /**
     * Unique username for the user account (5-50 characters).
     */
    @NotBlank(message = "LOCALIZE: USERNAME MUST NOT BE BLANK")
    @Size(min = 5, max = 50, message = "LOCALIZE: USERNAME MUST BE BETWEEN 5 AND 50 CHARACTERS")
    private String username;

    /**
     * User's email address for authentication and notifications.
     * Must be a valid email format.
     */
    @NotBlank(message = "LOCALIZE: EMAIL MUST NOT BE BLANK")
    @Email(message = "LOCALIZE: EMAIL MUST BE A VALID EMAIL ADDRESS")
    private String email;

    /**
     * Plain text password for user authentication (8-100 characters).
     * Will be hashed before storage.
     */
    @NotBlank(message = "LOCALIZE: PASSWORD MUST NOT BE BLANK")
    @Size(min = 8, max = 100, message = "LOCALIZE: PASSWORD MUST BE BETWEEN 8 AND 100 CHARACTERS")
    private String password;

    /**
     * User's first name (maximum 100 characters).
     */
    @NotBlank(message = "LOCALIZE: FIRSTNAME MUST NOT BE BLANK")
    @Size(max = 100, message = "LOCALIZE: FIRSTNAME MUST NOT BE GREATER THAN 100 CHARACTERS")
    private String firstName;

    /**
     * User's last name (maximum 100 characters).
     */
    @NotBlank(message = "LOCALIZE: LASTNAME MUST NOT BE BLANK")
    @Size(max = 100, message = "LOCALIZE: LASTNAME MUST NOT BE GREATER THAN 100 CHARACTERS")
    private String lastName;

    /**
     * User's birth date for age verification.
     * Must be in the past.
     */
    @NotNull(message = "LOCALIZE: BIRTHDATE MUST NOT BE BLANK")
    @Past(message = "LOCALIZE: BIRTHDATE MUST BE A PAST DATE")
    private LocalDate birthDate;
}

