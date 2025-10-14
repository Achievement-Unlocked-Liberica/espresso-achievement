package espresso.challenge.domain.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Small-sized Data Transfer Object interface for Challenge entities.
 * Contains minimal essential information for list views and summary displays.
 * Part of the size-based DTO pattern (SM/MD/LG) for controlling data transfer
 * volume.
 */
public interface ChallengeDtoSm {

    /**
     * The 7-character alphanumeric unique key of the challenge.
     * 
     * @return The challenge's entity key
     */
    String getEntityKey();

    /**
     * The title or name of the challenge.
     * 
     * @return The challenge title
     */
    String getTitle();

    /**
     * A detailed description of what the challenge represents.
     * 
     * @return The challenge description
     */
    String getDescription();

    /**
     * The date when this challenge will be fulfilled by the user.
     * 
     * @return The fulfillment date formatted as ISO 8601
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date getFulfillmentDate();

    /**
     * List of skill abbreviations associated with this challenge.
     * 
     * @return List of skill codes
     */
    List<String> getSkills();
}
