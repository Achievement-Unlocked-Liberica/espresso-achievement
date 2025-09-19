package espresso.achievement.domain.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import espresso.achievement.domain.events.AchievementCelebrationEvent;
import espresso.achievement.domain.events.AchievementCommentEvent;
import espresso.achievement.domain.events.AchievementEvent;
import espresso.achievement.domain.events.AchievementMediaEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.models.DomainAggregate;
import espresso.common.domain.support.StringListConverter;
import espresso.user.domain.entities.User;
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
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity(name = "Achievement")
@Table(name = "Achievements", indexes = {
        @Index(name = "idx_achievement_registered_at_desc", columnList = "registeredAt DESC"),
        @Index(name = "idx_achievement_id_pkey", columnList = "id", unique = true),
        @Index(name = "idx_achievement_entitykey_ukey", columnList = "entityKey", unique = true)
})
// @Table(name = "achievements", indexes = {@Index(name = "achievement_idx",
// columnList = "key", unique = true)})
public class Achievement extends DomainAggregate {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "completeddate", nullable = false)
    private Date completedDate;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    @Convert(converter = StringListConverter.class)
    private List<String> skills;

    @JsonManagedReference
    @OneToMany(mappedBy = "achievement", fetch = FetchType.LAZY)
    private List<AchievementMedia> media;

    @JsonManagedReference
    @OneToMany(mappedBy = "achievement", fetch = FetchType.LAZY)
    private List<AchievementComment> comments;

    @JsonManagedReference
    @OneToMany(mappedBy = "achievement", fetch = FetchType.LAZY)
    private List<AchievementCelebration> celebrations = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private AchievementVisibilityStatus achievementVisibility;

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
            User user, List<String> skills) {

        Achievement entity = new Achievement();

        entity.initializeEntity();

        entity.title = title;
        entity.description = description;
        entity.completedDate = completedDate;
        entity.achievementVisibility = isPublic
                ? AchievementVisibilityStatus.EVERYONE
                : AchievementVisibilityStatus.PRIVATE;

        entity.setUser(user);
        entity.setSkills(skills);

        entity.raiseAchievementCreated();

        return entity;
    }

    private void initializeEntity() {
        this.setEntityKey(espresso.common.domain.support.KeyGenerator.generateKey(7));
    }

    // public void setSkills(List<String> skills) {
    // this.skills = skills;
    // }

    // public void setMedia(AchievementMedia media) {
    // this.media = media;
    // }

    /**
     * Updates the achievement with new values for title, description, skills, and
     * visibility.
     * This method allows updating the main properties of an achievement after it
     * has been created.
     * 
     * @param title       The new title for the achievement
     * @param description The new description for the achievement
     * @param skills      List of skill abbreviations associated with the
     *                    achievement
     * @param isPublic    Whether the achievement should be publicly visible
     */
    public void update(String title, String description, List<String> skills, boolean isPublic) {
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.achievementVisibility = isPublic
                ? AchievementVisibilityStatus.EVERYONE
                : AchievementVisibilityStatus.PRIVATE;

        this.updateEntity();

        this.raiseAchievementUpdated();
    }

    /**
     * Disables the achievement by setting the enabled property to false.
     * This removes the achievement from all filters, searches, and visibility
     * without deleting it from the database.
     */
    public void disable() {
        this.setEnabled(false);

        this.updateEntity();

        this.raiseAchievementDisabled();
    }

    /**
     * Marks the achievement for deletion and raises a delete event.
     * This method should be called before the actual database deletion occurs.
     */
    public void delete() {
        this.updateEntity();

        this.raiseAchievementDeleted();
    }

    /**
     * Adds a comment to this achievement.
     * This method adds the comment to the internal list and raises a comment added
     * event.
     * 
     * @param comment The comment being added to this achievement
     */
    public void addComment(AchievementComment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }

        this.comments.add(comment);

        this.updateEntity();

        this.raiseCommentAdded(comment);
    }

    /**
     * Adds a celebration to this achievement from another user.
     * This method adds the celebration to the internal list.
     * 
     * @param celebration The celebration being added to this achievement
     */
    public void addCelebration(AchievementCelebration celebration) {
        if (this.celebrations == null) {
            this.celebrations = new ArrayList<>();
        }

        this.celebrations.add(celebration);

        this.updateEntity();

        this.raiseCelebrationAdded(celebration);
    }

    /**
     * Adds media to this achievement.
     * This method adds the media to the internal list and raises a media added event.
     * 
     * @param media The media being added to this achievement
     */
    public void addMedia(AchievementMedia media) {
        if (this.media == null) {
            this.media = new ArrayList<>();
        }

        this.media.add(media);

        this.updateEntity();

        this.raiseMediaAdded(media);
    }

    // #region Domain Events

    private void raiseCommentAdded(AchievementComment comment) {
        this.domainEvents.add(
                AchievementCommentEvent.create(
                        EventActionTypes.CREATED,
                        comment.getAchievement().getEntityKey(),
                        comment.getUser().getEntityKey(),
                        comment.getText()));
    }

    private void raiseCelebrationAdded(AchievementCelebration celebration) {

        this.domainEvents.add(
                AchievementCelebrationEvent.create(
                        EventActionTypes.CREATED,
                        celebration.getAchievementKey(),
                        celebration.getUserKey(),
                        celebration.getCount()));
    }

    private void raiseMediaAdded(AchievementMedia media) {
        this.domainEvents.add(
                AchievementMediaEvent.create(
                        EventActionTypes.CREATED,
                        media.getAchievement().getEntityKey(),
                        media.getAchievement().getUser().getEntityKey(),
                        media.getImageKey(),
                        media.getOriginalImageName(),
                        media.getContentType(),
                        media.getFileSize()));
    }

    private void raiseAchievementCreated() {
        this.domainEvents.add(
                AchievementEvent.create(
                        EventActionTypes.CREATED,
                        entityKey,
                        user.getEntityKey(),
                        title,
                        description,
                        completedDate,
                        skills.toArray(new String[0])));
    }

    private void raiseAchievementUpdated() {
        this.domainEvents.add(
                AchievementEvent.create(
                        EventActionTypes.UPDATED,
                        entityKey,
                        user.getEntityKey(),
                        title,
                        description,
                        completedDate,
                        skills.toArray(new String[0])));
    }

    private void raiseAchievementDisabled() {
        this.domainEvents.add(
                AchievementEvent.create(
                        EventActionTypes.DISABLED,
                        entityKey,
                        user.getEntityKey(),
                        title,
                        description,
                        completedDate,
                        skills.toArray(new String[0])));
    }

    private void raiseAchievementDeleted() {
        this.domainEvents.add(
                AchievementEvent.create(
                        EventActionTypes.DELETED,
                        entityKey,
                        user.getEntityKey(),
                        title,
                        description,
                        completedDate,
                        skills.toArray(new String[0])));
    }

    // #endregion Domain Events
}
