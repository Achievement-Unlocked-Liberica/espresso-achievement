package espresso.security.domain.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class AuthCredentialsCommand extends CommonCommand {

    @NotBlank(message = "LOCALIZE: USERNAME MUST NOT BE BLANK")
    @Size(min = 5, max = 50, message = "LOCALIZE: USERNAME MUST BE BETWEEN 5 AND 50 CHARACTERS")
    private String username;

    @NotBlank(message = "LOCALIZE: PASSWORD MUST NOT BE BLANK")
    @Size(min = 8, max = 100, message = "LOCALIZE: PASSWORD MUST BE BETWEEN 8 AND 100 CHARACTERS")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "LOCALIZE: PASSWORD MUST CONTAIN AT LEAST ONE LOWERCASE LETTER, ONE UPPERCASE LETTER, ONE NUMBER, AND ONE SPECIAL CHARACTER (@$!%*?&)"
    )
    private String password;
}
