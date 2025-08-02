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
    private String username;

    @NotBlank(message = "LOCALIZE: PASSWORD MUST NOT BE BLANK")
    private String password;
}
