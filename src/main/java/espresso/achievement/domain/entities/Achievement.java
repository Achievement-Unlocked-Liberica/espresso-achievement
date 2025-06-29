package espresso.achievement.domain.entities;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import espresso.achievement.domain.events.NewAchievementCreated;
import espresso.common.domain.models.DomainAggregate;
import espresso.common.domain.models.DomainEntity;
import espresso.common.domain.support.StringListConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents an achievement entity.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Achievement")
@Table(name = "Achievements")
// @Table(name = "achievements", indexes = {@Index(name = "achievement_idx", columnList = "key", unique = true)})
public class Achievement extends DomainEntity {

    
    String title;
    String description;
    Date completedDate;

    @Convert(converter = StringListConverter.class)
    List<String> skills;

    //@JsonManagedReference 
    //@OneToMany(mappedBy = "achievement", cascade = CascadeType.ALL)
    @Transient
    List<AchievementMedia> media;

    //@OneToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "userProfileId")
    @Transient
    UserProfile userProfile;

    @Enumerated(EnumType.STRING)
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
            this.initializeEntity();
        }
    }

    public static Achievement create(String title, String description, Date completedDate, boolean isPublic,
            UserProfile userProfile, List<String> skills) {

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

        entity.raiseNewAchievementCreatedEvent();

        return entity;
    }

    private void initializeEntity() {
        this.setEntityKey(KeyGenerator.generateShortString());
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public void setMedia(List<AchievementMedia> media) {
        this.media = media;
    }

    // #region Domain Events

    public void raiseNewAchievementCreatedEvent() {
        // this.domainEvents.add(new NewAchievementCreated(
        //         this.getEntityKey(),
        //         this.getUserProfile().getEntityKey(),
        //         this.getCompletedDate(),
        //         this.getSkills().stream().toArray(String[]::new),
        //         this.getMedia().stream().map(AchievementMedia::getEntityKey).toArray(String[]::new)
        //     ));
    }

    // #endregion Domain Events
}
