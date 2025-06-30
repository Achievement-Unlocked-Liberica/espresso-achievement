package espresso.user.domain.queries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import espresso.common.domain.queries.CommonQuery;
import espresso.common.domain.queries.QuerySizeType;

@Getter
@AllArgsConstructor
public class GetUserByKeyQuery extends CommonQuery {

    @NotBlank(message = "LOCALIZE: ENTITY KEY MUST NOT BE BLANK")
    @Size(min = 7, max = 7, message = "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS")
    private String entityKey;

    private QuerySizeType size;
}