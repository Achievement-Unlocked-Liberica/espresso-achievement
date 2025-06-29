package espresso.achievement.domain.entities;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import espresso.common.domain.models.DomainEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper = false)
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
// @Entity(name = "AchievementMedia")
// @Table(name = "AchievementMedias")
public class AchievementMedia extends DomainEntity {

    private boolean isUploaded;
    private String mediaPath;
    private String originalName;
    private String originalHash;
    private String mimeType;
    private Integer size;
    private String encoding;

    // @JsonBackReference
    // @ManyToOne
    // @JoinColumn(name = "achievementId", nullable = false)
    // private Achievement achievement;

    public static AchievementMedia createPreMedia(String originalName, String mimeType, String encoding, Integer size) {

        AchievementMedia preMedia = new AchievementMedia();
 
        preMedia.setOriginalName(originalName);
        preMedia.setMimeType(mimeType);
        preMedia.setSize(size);
        preMedia.setEncoding(encoding);

        preMedia.setEntityKey(KeyGenerator.generateShortString());

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