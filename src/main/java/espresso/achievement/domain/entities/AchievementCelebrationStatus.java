package espresso.achievement.domain.entities;

/**
 * Enumeration representing the status of an achievement celebration.
 * Used to track the processing state of celebration records.
 */
public enum AchievementCelebrationStatus {
    /** Celebration is pending processing */
    PENDING,
    
    /** Celebration has been approved and processed */
    APPROVED,
    
    /** Celebration has been flagged for review */
    FLAGGED,
    
    /** Celebration has been deleted */
    DELETED
}
