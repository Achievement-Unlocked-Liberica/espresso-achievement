package espresso.achievement.domain.entities;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AchievementMedia extends Entity {

    @Indexed(name = "achievement_media_idx",  unique = false)
    @Setter
    private String key;

    private boolean isUploaded;
    private String mediaPath;
    private String originalName;
    private String originalHash;
    private String mimeType;
    private Integer size;
    private String encoding;

    public static AchievementMedia createPreMedia(String originalName, String mimeType, String encoding, Integer size) {

        AchievementMedia preMedia = new AchievementMedia(
                null,
                false,
                null,
                originalName,
                originalName.hashCode() + "",
                mimeType,
                size,
                encoding);

        preMedia.setKey(KeyGenerator.generateShortString());

        return preMedia;
    }

}


/*
    private String storageUrl;
    private String containerName;
    private String mediaPath;
    private String accessToken;


PreMedia preMedia = new PreMedia(
    appConfiguration.getCloudConfig().getStorageUrl(),
    "media-files",
    "/pre-media/" + achievement.getKey() + "/uploaded",
    KeyGenerator.generateKeyString(24)
);
*/