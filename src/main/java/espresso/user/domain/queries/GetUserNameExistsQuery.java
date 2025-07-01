package espresso.user.domain.queries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import espresso.common.domain.queries.CommonQuery;

@Getter
@AllArgsConstructor
public class GetUserNameExistsQuery extends CommonQuery {

    @NotBlank(message = "LOCALIZE: USERNAME MUST NOT BE BLANK")
    @Size(min = 5, max = 50, message = "LOCALIZE: USERNAME MUST BE BETWEEN 5 AND 50 CHARACTERS")
    private String username;
}
