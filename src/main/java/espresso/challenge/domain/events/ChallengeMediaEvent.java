package espresso.challenge.domain.events;

import java.time.OffsetDateTime;

import espresso.common.domain.events.CommonEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.support.KeyGenerator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Event raised when media is added to a challenge.
 * This event is used to notify other systems about challenge media uploads.
 */
@Getter
@SuperBuilder
public class ChallengeMediaEvent extends CommonEvent {

    /**
     * The 7-character alphanumeric key of the challenge the media was added to.
     */
    private String challengeKey;
    
    /**
     * The 7-character alphanumeric key of the user who uploaded the media.
     */
    private String userKey;
    
    /**
     * The unique key identifying the media file.
     */
    private String mediaKey;
    
    /**
     * The URL where the media file can be accessed.
     */
    private String mediaUrl;
    
    /**
     * The original filename of the uploaded media as provided by the user.
     */
    private String originalImageName;
    
    /**
     * The MIME content type of the media file (e.g., image/jpeg, image/png).
     */
    private String contentType;
    
    /**
     * The size of the media file in bytes.
     */
    private Long fileSize;

    /**
     * Creates a new challenge media event.
     * 
     * @param eventType         The type of the event (for example: created, updated,
     *                          deleted...)
     * @param challengeKey      The key of the challenge the media was added to
     * @param userKey           The key of the user who uploaded the media
     * @param mediaKey          The unique key of the media file
     * @param mediaUrl          The URL where the media can be accessed
     * @param originalImageName The original filename of the uploaded media
     * @param contentType       The MIME type of the media file
     * @param fileSize          The size of the media file in bytes
     * @return A new ChallengeMediaEvent instance
     */
    public static ChallengeMediaEvent create(EventActionTypes eventType, String challengeKey, String userKey, 
            String mediaKey, String mediaUrl, String originalImageName, String contentType, Long fileSize) {
        return ChallengeMediaEvent.builder()
                .eventId(KeyGenerator.generateKey(7))
                .timestamp(OffsetDateTime.now())
                .eventType("Challenge.Media." + eventType.name())
                .source("challenge-module")
                .challengeKey(challengeKey)
                .userKey(userKey)
                .mediaKey(mediaKey)
                .mediaUrl(mediaUrl)
                .originalImageName(originalImageName)
                .contentType(contentType)
                .fileSize(fileSize)
                .build();
    }
}
