package espresso.achievement.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Key Transfer Object interface for Achievement entities.
 * Contains only the essential identifiers needed for references and lookups.
 * Used when only basic identification is needed without full entity data.
 */
public interface AchievementKto {

    /**
     * The database primary key identifier of the achievement.
     * 
     * @return The achievement's unique ID
     */
    @JsonIgnore
    Long getId();

    /**
     * The 7-character alphanumeric unique key of the achievement.
     * 
     * @return The achievement's entity key
     */
    String getEntityKey();
}
