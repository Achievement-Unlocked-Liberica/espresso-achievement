package espresso.achievement.domain.entities;

import java.util.List;

import espresso.user.domain.entities.UserDtoSm;

/**
 * Small-sized Data Transfer Object interface for Achievement entities.
 * Contains minimal essential information for list views and summary displays.
 * Part of the size-based DTO pattern (SM/MD/LG) for controlling data transfer volume.
 */
public interface AchievementDtoSm {
    
    /**
     * The 7-character alphanumeric unique key of the achievement.
     * @return The achievement's entity key
     */
    String getEntityKey();
    
    /**
     * The title or name of the achievement.
     * @return The achievement title
     */
    String getTitle();
    
    /**
     * A detailed description of what the achievement represents.
     * @return The achievement description
     */
    String getDescription();

    /**
     * List of skill abbreviations associated with this achievement.
     * @return List of skill codes
     */
    List<String> getSkills();

    /**
     * Basic information about the user who created this achievement.
     * @return User summary information
     */
    UserDtoSm getUser();

    /**
     * Collection of media files associated with this achievement.
     * @return List of media summary information
     */
    List<AchievementMediaDtoSm> getMedia();
}
