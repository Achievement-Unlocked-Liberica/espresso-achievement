package espresso.achievement.domain.queries;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import espresso.common.domain.queries.CommonQuery;
import espresso.common.domain.queries.QuerySizeType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetLatestAchievementsQuery extends CommonQuery {

    @NotNull(message = "LOCALIZE: DTO SIZE MUST NOT BE NULL")
    private QuerySizeType dtoSize;
    
    // Optional limit parameter - defaults to 10 if not specified
    private Integer limit = 10;
    
    public GetLatestAchievementsQuery(QuerySizeType dtoSize) {
        this.dtoSize = dtoSize;
        this.limit = 10;
    }
}
