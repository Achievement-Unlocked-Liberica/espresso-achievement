package espresso.achievement.domain.entities;

/**
 * Enumeration representing the status of an achievement comment.
 * Used to track the lifecycle and moderation state of comments.
 */
public enum AchievementCommentStatus {
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
