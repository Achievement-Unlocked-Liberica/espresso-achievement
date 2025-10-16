package espresso.challenge.domain.entities;

/**
 * Knowledge Transfer Object (KTO) interface for ChallengeMedia entities.
 * Provides read-only access to essential ChallengeMedia properties.
 */
public interface ChallengeMediaKto {
    
    /**
     * Gets the unique identifier of the media record.
     * 
     * @return The media ID
     */
    Long getId();
    
    /**
     * Gets the unique key of the image.
     * 
     * @return The image key (combination of challenge key and random key)
     */
    String getImageKey();
    
    /**
     * Gets the URL where the media can be accessed.
     * 
     * @return The media URL
     */
    String getMediaUrl();
}
