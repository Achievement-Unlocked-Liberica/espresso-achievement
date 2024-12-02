package espresso.achievement.domain.readModels;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AchievementDetailReadModel {

    private final String key;
    private final String title;
    private final String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date completedDate;
    private final SkillDetailReadModel[] skills;
    // private final AchievementMediaDetailReadModel[] media;
    private final UserProfileDetailReadModel userProfile;
    // private AchievementVisibilityStatus achievementVisibility;

    public static AchievementDetailReadModel createMockInstance(String key) {
        AchievementDetailReadModel mockInstance = new AchievementDetailReadModel(
                key,
                "mockTitle",
                "mockDescription",
                new Date(),
                new SkillDetailReadModel[] {
                        new SkillDetailReadModel("str0000", "str", "strength"),
                        new SkillDetailReadModel("dex0000", "dex", "dexterity") },
                new UserProfileDetailReadModel(
                        "mockKey",
                        "mockUserName",
                        "mockEmail",
                        "mockFirstName",
                        "mockLastName"));

        return mockInstance;
    }
}
