package espresso.achievement.domain.entities;

/**
 * Enumeration representing the visibility levels for achievements.
 * Defines who can view and interact with an achievement based on the creator's privacy preferences.
 */
public enum AchievementVisibilityStatus {
    /** Unknown visibility status (default/unset) */
    UNKNOWN,
    
    /** Visible only to the achievement creator */
    PRIVATE,
    
    /** Visible to the creator's direct friends */
    FRIENDS,
    
    /** Visible to friends and friends of friends */
    FRIENDS_OF_FRIENDS,
    
    /** Visible to all users on the platform */
    EVERYONE
}
