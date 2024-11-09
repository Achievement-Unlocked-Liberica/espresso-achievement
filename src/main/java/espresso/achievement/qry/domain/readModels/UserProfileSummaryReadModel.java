package espresso.achievement.qry.domain.readModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileSummaryReadModel {
    private final String key;
    private final String userName;
}
