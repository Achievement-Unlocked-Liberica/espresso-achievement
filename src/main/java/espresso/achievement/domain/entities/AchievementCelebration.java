package espresso.achievement.domain.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import espresso.common.domain.models.ValueEntity;
import espresso.user.domain.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a celebration given to an achievement by a user.
 * This is a value entity that captures the act of celebrating another user's achievement.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name = "AchievementCelebration")
@Table(name = "AchievementCelebrations", indexes = {
        @Index(name = "idx_achievement_celebration_id_pkey", columnList = "id", unique = true),
        @Index(name = "idx_achievement_celebration_created_at_desc", columnList = "createdAt DESC")
})
public class AchievementCelebration extends ValueEntity {

    /**
     * Unique identifier for the celebration record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The number of celebrations given (1-9)
     */
    @Column(name = "count", nullable = false)
    private Integer count;

    /**
     * The achievement being celebrated
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievementId", referencedColumnName = "id")
    private Achievement achievement;

    /**
     * The user giving the celebration
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    /**
     * The 7-character alphanumeric key of the achievement being celebrated
     */
    @Column(name = "achievementKey", nullable = false, length = 7)
    private String achievementKey;

    /**
     * The 7-character alphanumeric key of the user giving the celebration
     */
    @Column(name = "userKey", nullable = false, length = 7)
    private String userKey;

    /**
     * Timestamp when the celebration was created (UTC)
     */
    @Column(name = "createdAt", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Timestamp when the celebration was last updated (UTC)
     */
    @Column(name = "updatedAt", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Current processing status of the celebration
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AchievementCelebrationStatus status;

    /**
     * Creates a new AchievementCelebration instance with the specified parameters.
     * Initializes timestamps and sets default status to PENDING.
     * 
     * @param count The number of celebrations to give (1-9)
     * @param achievement The achievement being celebrated
     * @param user The user giving the celebration
     * @return A new AchievementCelebration instance ready to be persisted
     */
    public static AchievementCelebration create(Integer count, Achievement achievement, User user) {
        AchievementCelebration celebration = new AchievementCelebration();
        
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        
        celebration.count = count;
        celebration.achievement = achievement;
        celebration.user = user;
        celebration.achievementKey = achievement.getEntityKey();
        celebration.userKey = user.getEntityKey();
        celebration.createdAt = now;
        celebration.updatedAt = now;
        celebration.status = AchievementCelebrationStatus.PENDING;
        
        return celebration;
    }
}
