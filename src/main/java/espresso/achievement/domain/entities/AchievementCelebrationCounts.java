package espresso.achievement.domain.entities;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the aggregated celebration counts for an achievement.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "AchievementCelebrationCounts")
@Table(name = "AchievementCelebrationCounts")
public class AchievementCelebrationCounts {

    /**
     * The primary key identifier for the celebration counts record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The foreign key reference to the achievement these counts belong to.
     */
    @Column(name = "achievementId", nullable = false)
    private Long achievementId;

    /**
     * The total number of celebrations that have been given to the achievement.
     */
    @Column(name = "aggregatedCount", nullable = false)
    private Integer aggregatedCount;

    /**
     * The timestamp when the celebration counts were last updated.
     */
    @Column(name = "updatedAt", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * The achievement entity that these celebration counts belong to.
     * Lazy-loaded to improve performance.
     */

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievementId", referencedColumnName = "id", insertable = false, updatable = false)
    private Achievement achievement;

    /**
     * Constructor for creating celebration counts for a specific achievement.
     * 
     * @param achievementId   The ID of the achievement
     * @param aggregatedCount The initial count of celebrations
     */
    public AchievementCelebrationCounts(Long achievementId, Integer aggregatedCount) {
        this.achievementId = achievementId;
        this.aggregatedCount = aggregatedCount != null ? aggregatedCount : 0;
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Updates the aggregated count and sets the updated timestamp.
     * 
     * @param newCount The new aggregated count
     */
    public void updateCount(Integer newCount) {
        this.aggregatedCount = newCount != null ? newCount : 0;
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Increments the aggregated count by the specified amount.
     * 
     * @param increment The amount to increment by
     */
    public void incrementCount(Integer increment) {
        this.aggregatedCount += (increment != null ? increment : 0);
        this.updatedAt = OffsetDateTime.now();
    }
}