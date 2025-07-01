package espresso.user.domain.queries;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import espresso.common.domain.queries.CommonQuery;

@Getter
@AllArgsConstructor
public class GetEmailExistsQuery extends CommonQuery {

    @NotBlank(message = "LOCALIZE: EMAIL MUST NOT BE BLANK")
    @Email(message = "LOCALIZE: EMAIL MUST BE A VALID EMAIL ADDRESS")
    private String email;
}
