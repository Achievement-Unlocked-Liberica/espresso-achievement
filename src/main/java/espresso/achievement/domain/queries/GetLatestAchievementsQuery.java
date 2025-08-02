package espresso.achievement.domain.queries;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

import espresso.common.domain.queries.CommonQuery;
import espresso.common.domain.queries.QuerySizeType;

@Getter
@AllArgsConstructor
public class GetLatestAchievementsQuery extends CommonQuery {

    @NotNull(message = "LOCALIZE: DTO SIZE MUST NOT BE NULL")
    private QuerySizeType size;
    
    private OffsetDateTime fromDate;
    
    // Optional limit parameter - defaults to 10 if not specified
    private Integer limit = 10;
}
