package espresso.achievement.domain.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import espresso.user.domain.entities.UserDtoSm;

/**
 * Small-sized Data Transfer Object interface for Achievement entities.
 * Contains minimal essential information for list views and summary displays.
 * Part of the size-based DTO pattern (SM/MD/LG) for controlling data transfer
 * volume.
 */
public interface AchievementDtoSm {

    /**
     * The 7-character alphanumeric unique key of the achievement.
     * 
     * @return The achievement's entity key
     */
    String getEntityKey();

    /**
     * The title or name of the achievement.
     * 
     * @return The achievement title
     */
    String getTitle();

    /**
     * A detailed description of what the achievement represents.
     * 
     * @return The achievement description
     */
    String getDescription();

    /**
     * The date when this achievement was completed by the user.
     * 
     * @return The completion date formatted as ISO 8601
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date getCompletedDate();

    /**
     * List of skill abbreviations associated with this achievement.
     * 
     * @return List of skill codes
     */
    List<String> getSkills();
}
