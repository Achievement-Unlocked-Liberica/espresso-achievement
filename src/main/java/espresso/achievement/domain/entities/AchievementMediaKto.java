package espresso.achievement.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface AchievementMediaKto {

        /**
     * The database primary key identifier of the achievement.
     * 
     * @return The achievement's unique ID
     */
    @JsonIgnore
    Long getId();
    
    /**
     * Unique key generated for the image, combining achievement key and random key.
     */
    String getImageKey();

    /**
     * The URL where the image can be accessed from external storage.
     */
    String getMediaUrl();
}
