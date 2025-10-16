package espresso.challenge.domain.entities;

/**
 * Enumeration representing the status of a challenge comment.
 * Used to track the lifecycle and moderation state of comments.
 */
public enum ChallengeCommentStatus {
    /**
     * Comment is awaiting moderation approval
     */
    PENDING,
    
    /**
     * Comment has been approved and is visible
     */
    APPROVED,
    
    /**
     * Comment has been flagged for review
     */
    FLAGGED,
    
    /**
     * Comment has been deleted/removed
     */
    DELETED
}
