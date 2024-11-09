package espresso.achievement.cmd.domain.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import espresso.achievement.cmd.domain.events.NewAchievementCreated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents an achievement entity.
 */
@ToString
@NoArgsConstructor
@Document(collection = "achievements")
public class Achievement extends Aggregate {

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

    /*
     * The constructor is package-private to prevent the creation of an achievement
     * by external classes.
     * This is to ensure that the entity is created through the factory method.
     * 
     * @param initializeEntity Indicates whether the entity should be initialized.
     */
    protected Achievement(boolean initializeEntity) {
        if (initializeEntity == true) {
            this.setTimestamp(new Date());
            this.setId(UUID.randomUUID());
            this.setKey(KeyGenerator.generateShortString());
        }
    }

    public static Achievement create(String title, String description, Date completedDate, boolean isPublic,
            UserProfile userProfile, List<Skill> skills, List<AchievementMedia> media) {

        Achievement entity = new Achievement();

        entity.initializeEntity();

        entity.title = title;
        entity.description = description;
        entity.completedDate = completedDate;
        entity.achievementVisibility = isPublic
                ? AchievementVisibilityStatus.EVERYONE
                : AchievementVisibilityStatus.PRIVATE;
        entity.userProfile = userProfile;

        entity.setSkills(skills);
        entity.setMedia(media);

        entity.raiseNewAchievementCreatedEvent();

        return entity;
    }

    private void initializeEntity() {
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

    // #region Domain Events



    public void raiseNewAchievementCreatedEvent() {
        this.domainEvents.add(new NewAchievementCreated(
                this.getKey(),
                this.getUserProfile().getKey(),
                this.getCompletedDate(),
                this.getSkills().stream().map(Skill::getKey).toArray(String[]::new),
                null //this.getMedia().stream().map(AchievementMedia::getKey).toArray(String[]::new)
                ));
    }

    // #endregion Domain Events
}
