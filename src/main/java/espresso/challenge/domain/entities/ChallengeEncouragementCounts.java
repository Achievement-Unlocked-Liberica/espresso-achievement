package espresso.challenge.domain.entities;

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
 * Represents the aggregated encouragement counts for a challenge.
 * This entity maintains a denormalized count of all encouragements given to a challenge
 * for performance optimization, avoiding the need to count individual encouragement records
 * when displaying challenge encouragement totals.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ChallengeEncouragementCounts")
@Table(name = "ChallengeEncouragementCounts")
public class ChallengeEncouragementCounts {

    /**
     * The primary key identifier for the encouragement counts record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The foreign key reference to the challenge these counts belong to.
     */
    @Column(name = "challengeId", nullable = false)
    private Long challengeId;

    /**
     * The total number of encouragements that have been given to the challenge.
     */
    @Column(name = "aggregatedCount", nullable = false)
    private Integer aggregatedCount;

    /**
     * The timestamp when the encouragement counts were last updated.
     */
    @Column(name = "updatedAt", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * The challenge entity that these encouragement counts belong to.
     * Lazy-loaded to improve performance.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challengeId", referencedColumnName = "id", insertable = false, updatable = false)
    private Challenge challenge;

    /**
     * Constructor for creating encouragement counts for a specific challenge.
     * 
     * @param challengeId     The ID of the challenge
     * @param aggregatedCount The initial count of encouragements
     */
    public ChallengeEncouragementCounts(Long challengeId, Integer aggregatedCount) {
        this.challengeId = challengeId;
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
