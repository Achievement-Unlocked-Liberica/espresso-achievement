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

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class AddUserCommand extends CommonCommand{

    @NotBlank(message = "LOCALIZE: USERNAME MUST NOT BE BLANK")
    @Size(min = 5, max = 50, message = "LOCALIZE: USERNAME MUST BE BETWEEN 5 AND 50 CHARACTERS")
    private String username;

    @NotBlank(message = "LOCALIZE: EMAIL MUST NOT BE BLANK")
    @Email(message = "LOCALIZE: EMAIL MUST BE A VALID EMAIL ADDRESS")
    private String email;

    @NotBlank(message = "LOCALIZE: PASSWORD MUST NOT BE BLANK")
    @Size(min = 8, max = 100, message = "LOCALIZE: PASSWORD MUST BE BETWEEN 8 AND 100 CHARACTERS")
    private String password;

    @NotBlank(message = "LOCALIZE: FIRSTNAME MUST NOT BE BLANK")
    @Size(max = 100, message = "LOCALIZE: FIRSTNAME MUST NOT BE GREATER THAN 100 CHARACTERS")
    private String firstName;

    @NotBlank(message = "LOCALIZE: LASTNAME MUST NOT BE BLANK")
    @Size(max = 100, message = "LOCALIZE: LASTNAME MUST NOT BE GREATER THAN 100 CHARACTERS")
    private String lastName;

    @NotNull(message = "LOCALIZE: BIRTHDATE MUST NOT BE BLANK")
    @Past(message = "LOCALIZE: BIRTHDATE MUST BE A PAST DATE")
    private LocalDate birthDate;
}

