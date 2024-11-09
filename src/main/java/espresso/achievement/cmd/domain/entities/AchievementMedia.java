package espresso.achievement.cmd.domain.entities;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AchievementMedia extends Entity {

    private boolean isUploaded;
    private String mediaPath;
    private String originalName;
    private String originalHash;
    private String mimeType;
    private Integer size;
    private String encoding;

    public static AchievementMedia createPreMedia(String originalName, String mimeType, String encoding, Integer size) {

        AchievementMedia preMedia = new AchievementMedia(
                false,
                null,
                originalName,
                originalName.hashCode() +"",
                mimeType,
                size,
                encoding);

        preMedia.setKey(KeyGenerator.generateShortString());

        return preMedia;
    }


}
