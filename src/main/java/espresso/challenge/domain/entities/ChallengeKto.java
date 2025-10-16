package espresso.challenge.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Key Transfer Object interface for Challenge entities.
 * Contains only the essential identifiers needed for references and lookups.
 * Used when only basic identification is needed without full entity data.
 */
public interface ChallengeKto {

    /**
     * The database primary key identifier of the challenge.
     * 
     * @return The challenge's unique ID
     */
    @JsonIgnore
    Long getId();

    /**
     * The 7-character alphanumeric unique key of the challenge.
     * 
     * @return The challenge's entity key
     */
    String getEntityKey();
}
