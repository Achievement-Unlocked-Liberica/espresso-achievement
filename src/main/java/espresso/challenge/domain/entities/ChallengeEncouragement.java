package espresso.challenge.domain.entities;

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
 * Represents an encouragement given to a challenge by a user.
 * This is a value entity that captures the act of encouraging another user to complete their challenge.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name = "ChallengeEncouragement")
@Table(name = "ChallengeEncouragements", indexes = {
        @Index(name = "idx_challenge_encouragement_id_pkey", columnList = "id", unique = true),
        @Index(name = "idx_challenge_encouragement_created_at_desc", columnList = "createdAt DESC")
})
public class ChallengeEncouragement extends ValueEntity {

    /**
     * Unique identifier for the encouragement record
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The number of encouragements given (1-9)
     */
    @Column(name = "count", nullable = false)
    private Integer count;

    /**
     * The challenge being encouraged
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challengeId", referencedColumnName = "id")
    private Challenge challenge;

    /**
     * The user giving the encouragement
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    /**
     * The 7-character alphanumeric key of the challenge being encouraged
     */
    @Column(name = "challengeKey", nullable = false, length = 7)
    private String challengeKey;

    /**
     * The 7-character alphanumeric key of the user giving the encouragement
     */
    @Column(name = "userKey", nullable = false, length = 7)
    private String userKey;

    /**
     * Timestamp when the encouragement was created (UTC)
     */
    @Column(name = "createdAt", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Timestamp when the encouragement was last updated (UTC)
     */
    @Column(name = "updatedAt", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Current processing status of the encouragement
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChallengeEncouragementStatus status;

    /**
     * Creates a new ChallengeEncouragement instance with the specified parameters.
     * Initializes timestamps and sets default status to PENDING.
     * 
     * @param count The number of encouragements to give (1-9)
     * @param challenge The challenge being encouraged
     * @param user The user giving the encouragement
     * @return A new ChallengeEncouragement instance ready to be persisted
     */
    public static ChallengeEncouragement create(Integer count, Challenge challenge, User user) {
        ChallengeEncouragement encouragement = new ChallengeEncouragement();
        
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        
        encouragement.count = count;
        encouragement.challenge = challenge;
        encouragement.user = user;
        encouragement.challengeKey = challenge.getEntityKey();
        encouragement.userKey = user.getEntityKey();
        encouragement.createdAt = now;
        encouragement.updatedAt = now;
        encouragement.status = ChallengeEncouragementStatus.PENDING;
        
        return encouragement;
    }
}
