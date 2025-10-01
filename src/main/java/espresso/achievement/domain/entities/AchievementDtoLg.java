package espresso.achievement.domain.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import espresso.user.domain.entities.UserDtoLg;

/**
 * Large-sized Data Transfer Object interface for Achievement entities.
 * Contains comprehensive detail for full achievement views and detailed pages.
 * Part of the size-based DTO pattern (SM/MD/LG) for controlling data transfer volume.
 * Currently not fully defined - placeholder for future requirements.
 */
public interface AchievementDtoLg {
    
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
     * The date when this achievement was completed by the user.
     * @return The completion date formatted as yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date getCompletedDate();
    
    /**
     * Detailed information about the user who created this achievement.
     * @return User detailed information
     */
    UserDtoLg getUser();
    
    /**
     * List of skill abbreviations associated with this achievement.
     * @return List of skill codes
     */
    List<String> getSkills();
    
    /**
     * The visibility status of the achievement (PRIVATE, EVERYONE, etc.).
     * @return The visibility setting
     */
    AchievementVisibilityStatus getAchievementVisibility();
    
    /**
     * Collection of media files (images) associated with this achievement.
     * @return List of detailed media information
     */
    List<AchievementMediaDtoLg> getMedia();
}
