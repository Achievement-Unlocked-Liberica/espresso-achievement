package espresso.challenge.domain.entities;

/**
 * Enumeration representing the status of a challenge encouragement.
 * Used to track the processing state of encouragement records.
 */
public enum ChallengeEncouragementStatus {
    /** Encouragement is pending processing */
    PENDING,
    
    /** Encouragement has been approved and processed */
    APPROVED,
    
    /** Encouragement has been flagged for review */
    FLAGGED,
    
    /** Encouragement has been deleted */
    DELETED
}
