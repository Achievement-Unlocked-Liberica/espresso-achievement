package espresso.achievement.domain.readModels;

import java.util.Date;

 

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Read model for detailed achievement information.
 * Contains comprehensive achievement data including user profile and skills.
 */
@Getter
@AllArgsConstructor
public class AchievementDetailReadModel {

    /**
     * The 7-character alphanumeric key of the achievement.
     */
    private final String key;
    
    /**
     * The title or name of the achievement.
     */
    private final String title;
    
    /**
     * A detailed description of the achievement.
     */
    private final String description;
    
    /**
     * The date when the achievement was completed.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date completedDate;
    
    /**
     * Array of skills associated with this achievement.
     */
    private final SkillDetailReadModel[] skills;
    
    /**
     * User profile information of the achievement owner.
     */
    private final UserProfileDetailReadModel userProfile;

    public static AchievementDetailReadModel createMockInstance(String key) {
    return new AchievementDetailReadModel(
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
    }
}
