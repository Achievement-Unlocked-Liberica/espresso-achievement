package espresso.achievement.cmd.domain.entities;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents an achievement entity.
 */
@ToString
@NoArgsConstructor
@Document(collection = "achievements")
public class Achievement extends Entity {
    @Getter
    String title;

    @Getter
    String description;

    @Getter
    Date completedDate;

    @Getter
    List<Skill> skills;

    @Getter
    List<AchievementMedia> media;

    @Getter
    UserProfile userProfile;

    @Getter
    AchievementVisibilityStatus achievementVisibility;

    /**
     * Constructs a new Achievement object with the specified title, description,
     * completed date, visibility status, and user profile.
     *
     * @param title         the title of the achievement
     * @param description   the description of the achievement
     * @param completedDate the date when the achievement was completed
     * @param isPublic      the visibility status of the achievement (true for
     *                      public, false for private)
     * @param userProfile   the user profile associated with the achievement
     */
    public Achievement(String title, String description, Date completedDate, boolean isPublic,
            UserProfile userProfile) {
        // This constructor is used to build an Achivement witht he minimal required
        // information to be created. The rest of the properties will be added later.
        this.title = title;
        this.description = description;
        this.completedDate = completedDate;
        this.achievementVisibility = isPublic
                ? AchievementVisibilityStatus.EVERYONE
                : AchievementVisibilityStatus.PRIVATE;

        this.userProfile = userProfile;

        this.setTimestamp(new Date());
        this.setId(UUID.randomUUID());
        this.setKey(KeyGenerator.generateShortString());
    }



    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public void setMedia(List<AchievementMedia> media) {
        this.media = media;
    }

    /**
     * Represents the visibility status of an achievement.
     */
    public enum AchievementVisibilityStatus {
        UNKNOWN,
        PRIVATE,
        FRIENDS,
        FRIENDS_OF_FRIENDS,
        EVERYONE
    }

}
