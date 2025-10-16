package espresso.challenge.domain.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import espresso.user.domain.entities.UserDtoLg;

/**
 * Large-sized Data Transfer Object interface for Challenge entities.
 * Contains comprehensive detail for full challenge views and detailed pages.
 * Part of the size-based DTO pattern (SM/MD/LG) for controlling data transfer volume.
 */
public interface ChallengeDtoLg {
    
    /**
     * The 7-character alphanumeric unique key of the challenge.
     * @return The challenge's entity key
     */
    String getEntityKey();
    
    /**
     * The title or name of the challenge.
     * @return The challenge title
     */
    String getTitle();
    
    /**
     * A detailed description of what the challenge represents.
     * @return The challenge description
     */
    String getDescription();
    
    /**
     * The date when this challenge will be fulfilled by the user.
     * @return The fulfillment date formatted as yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date getFulfillmentDate();
    
    /**
     * Detailed information about the user who created this challenge.
     * @return User detailed information
     */
    UserDtoLg getUser();
    
    /**
     * List of skill abbreviations associated with this challenge.
     * @return List of skill codes
     */
    List<String> getSkills();
    
    /**
     * The visibility status of the challenge (PRIVATE, EVERYONE, etc.).
     * @return The visibility setting
     */
    ChallengeVisibilityStatus getChallengeVisibility();
    
    /**
     * Collection of media files associated with this challenge.
     * @return List of detailed media information
     */
    List<ChallengeMediaKto> getMedia();
}
