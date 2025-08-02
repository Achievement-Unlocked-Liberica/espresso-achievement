package espresso.achievement.domain.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
import espresso.common.domain.models.DomainEntity;
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
public class Achievement extends DomainEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;
    
    @Column(name = "completeddate", nullable = false)
    private Date completedDate;

    private OffsetDateTime registeredAt;
    private boolean active;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    @Convert(converter = StringListConverter.class)
    private List<String> skills;

    @JsonManagedReference
    @OneToMany(mappedBy = "achievement", fetch = FetchType.LAZY)
    private List<AchievementMedia> media;

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

        entity.raiseNewAchievementCreatedEvent();

        return entity;
    }

    private void initializeEntity() {
        this.setEntityKey(espresso.common.domain.support.KeyGenerator.generateKey(7));
        this.registeredAt = OffsetDateTime.now(ZoneOffset.UTC);
        this.active = true;
    }

    // public void setSkills(List<String> skills) {
    // this.skills = skills;
    // }

    // public void setMedia(AchievementMedia media) {
    // this.media = media;
    // }

    // #region Domain Events

    public void raiseNewAchievementCreatedEvent() {
        // this.domainEvents.add(new NewAchievementCreated(
        // this.getEntityKey(),
        // this.getUserProfile().getEntityKey(),
        // this.getCompletedDate(),
        // this.getSkills().stream().toArray(String[]::new),
        // this.getMedia().stream().map(AchievementMedia::getEntityKey).toArray(String[]::new)
        // ));
    }

    // #endregion Domain Events
}
