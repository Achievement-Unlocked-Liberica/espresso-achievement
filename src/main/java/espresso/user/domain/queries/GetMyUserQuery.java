package espresso.user.domain.queries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import espresso.common.domain.queries.CommonQuery;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetMyUserQuery extends CommonQuery {

    @NotBlank(message = "LOCALIZE: ENTITY KEY MUST NOT BE BLANK")
    private String entityKey;
}
