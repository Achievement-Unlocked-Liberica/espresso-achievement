package espresso.achievement.qry.domain.readModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillDetailReadModel {
    private final String key;
    private final String abreviation;
    private final String name;
}
