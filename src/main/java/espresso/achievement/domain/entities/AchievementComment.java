package espresso.achievement.domain.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import espresso.common.domain.models.ValueEntity;
import espresso.user.domain.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
 * Entity representing a comment on an achievement.
 * Contains the comment text, metadata, and relationships to Achievement and User entities.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name = "AchievementComment")
@Table(name = "AchievementComments", indexes = {
        @Index(name = "idx_achievement_comment_id_pkey", columnList = "id", unique = true),
        @Index(name = "idx_achievement_comment_created_at_desc", columnList = "createdAt DESC"),
        @Index(name = "idx_achievement_comment_achievement_id_idx", columnList = "achievementId"),
        @Index(name = "idx_achievement_comment_user_id_idx", columnList = "userId")
})
public class AchievementComment extends ValueEntity {

    /**
     * Primary key - auto-incrementing long value
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The text content of the comment (maximum 200 characters)
     */
    @Column(name = "text", nullable = false, length = 200)
    private String text;

    /**
     * Reference to the achievement this comment belongs to.
     * Uses lazy loading to prevent unnecessary data fetching.
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievementId", referencedColumnName = "id")
    private Achievement achievement;

    /**
     * Reference to the user who posted the comment.
     * Uses lazy loading to prevent unnecessary data fetching.
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    /**
     * UTC timestamp when the comment was created
     */
    @Column(name = "createdAt", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * UTC timestamp when the comment was last updated
     */
    @Column(name = "updatedAt", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Sentiment analysis results for the comment text
     */
    @Embedded
    private CommentSentiment sentiment;

    /**
     * ISO 639 language code of the comment text (e.g., "en", "es", "fr")
     */
    @Column(name = "language", nullable = false, length = 5)
    private String language;

    /**
     * Current status of the comment for moderation purposes
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AchievementCommentStatus status;

    /**
     * Static factory method to create a new AchievementComment instance.
     * Initializes all required fields with appropriate default values.
     * 
     * @param text The comment text content
     * @param achievement The achievement being commented on
     * @param user The user posting the comment
     * @return A new AchievementComment instance ready for persistence
     */
    public static AchievementComment create(String text, Achievement achievement, User user) {
        AchievementComment comment = new AchievementComment();

        // Set the comment content and relationships
        comment.setText(text);
        comment.setAchievement(achievement);
        comment.setUser(user);

        // Initialize system fields
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        // Set default sentiment (neutral) - can be updated later by sentiment analysis
        comment.setSentiment(CommentSentiment.createDefault());

        // Set default language to English - can be updated by language detection
        comment.setLanguage("en");

        // Set initial status to pending for moderation
        comment.setStatus(AchievementCommentStatus.PENDING);

        return comment;
    }

    /**
     * Updates the comment's updatedAt timestamp to current UTC time.
     * Should be called whenever the comment is modified.
     */
    public void markAsUpdated() {
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Updates the comment's sentiment analysis results.
     * 
     * @param sentiment The new sentiment analysis results
     */
    public void updateSentiment(CommentSentiment sentiment) {
        this.sentiment = sentiment;
        markAsUpdated();
    }

    /**
     * Updates the comment's detected language.
     * 
     * @param language ISO 639 language code
     */
    public void updateLanguage(String language) {
        this.language = language;
        markAsUpdated();
    }

    /**
     * Updates the comment's moderation status.
     * 
     * @param status The new comment status
     */
    public void updateStatus(AchievementCommentStatus status) {
        this.status = status;
        markAsUpdated();
    }
}
