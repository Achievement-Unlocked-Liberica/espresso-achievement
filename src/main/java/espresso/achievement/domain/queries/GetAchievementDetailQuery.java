package espresso.achievement.domain.queries;

import java.time.OffsetDateTime;

import espresso.common.domain.queries.CommonQuery;
import espresso.common.domain.queries.QuerySizeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAchievementDetailQuery extends CommonQuery {

    @NotNull(message = "LOCALIZE: THE SIZE MUST NOT BE NULL")
    private QuerySizeType size;

    @NotNull(message = "LOCALIZE: THE KEY MUST NOT BE NULL")
    @Size(min = 7, max = 7, message = "LOCALIZE: THE KEY MUST BE 7 CHARACTERS")
    private String entityKey;
}