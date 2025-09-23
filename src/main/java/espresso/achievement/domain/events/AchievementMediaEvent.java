package espresso.achievement.domain.events;

import java.time.OffsetDateTime;

import espresso.common.domain.events.CommonEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.support.KeyGenerator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Event raised when media is added to an achievement.
 * This event is used to notify other systems about achievement media uploads.
 */
@Getter
@SuperBuilder
public class AchievementMediaEvent extends CommonEvent {

    /**
     * The 7-character alphanumeric key of the achievement the media was added to.
     */
    private String achievementKey;
    
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
     * Creates a new achievement media event.
     * 
     * @param eventType         The type of the event (for example: created, updated,
     *                          deleted...)
     * @param achievementKey    The key of the achievement the media was added to
     * @param userKey           The key of the user who uploaded the media
     * @param mediaKey          The unique key of the media file
     * @param originalImageName The original filename of the uploaded media
     * @param contentType       The MIME type of the media file
     * @param fileSize          The size of the media file in bytes
     * @return A new AchievementMediaEvent instance
     */
    public static AchievementMediaEvent create(EventActionTypes eventType, String achievementKey, String userKey, 
            String mediaKey, String mediaUrl, String originalImageName, String contentType, Long fileSize) {
        return AchievementMediaEvent.builder()
                .eventId(KeyGenerator.generateKey(7))
                .timestamp(OffsetDateTime.now())
                .eventType("Achievement.Media." + eventType.name())
                .source("achievement-module")
                .achievementKey(achievementKey)
                .userKey(userKey)
                .mediaKey(mediaKey)
                .mediaUrl(mediaUrl)
                .originalImageName(originalImageName)
                .contentType(contentType)
                .fileSize(fileSize)
                .build();
    }
}