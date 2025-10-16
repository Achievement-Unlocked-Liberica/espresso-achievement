package espresso.challenge.domain.entities;

/**
 * Enumeration representing the visibility levels for challenges.
 * Defines who can view and interact with a challenge based on the creator's privacy preferences.
 */
public enum ChallengeVisibilityStatus {
    /** Unknown visibility status (default/unset) */
    UNKNOWN,
    
    /** Visible only to the challenge creator */
    PRIVATE,
    
    /** Visible to the creator's direct friends */
    FRIENDS,
    
    /** Visible to friends and friends of friends */
    FRIENDS_OF_FRIENDS,
    
    /** Visible to all users on the platform */
    EVERYONE
}
