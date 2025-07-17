package espresso.achievement.domain.entities;

import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Lob;

import espresso.common.domain.models.DomainEntity;
import espresso.common.domain.models.ValueEntity;
import espresso.common.domain.support.KeyGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@EqualsAndHashCode(callSuper = false)
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "AchievementMedia")
@Table(name = "AchievementMedias")
public class AchievementMedia extends ValueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @OneToOne(mappedBy = "media")
    private Achievement achievement;

    @Column(name = "imageName", nullable = false)
    private String imageName;

    @Column(name = "contentType")
    private String contentType;

    @Lob
    @Column(name = "imageData")
    private byte[] imageData;

    @Column(name = "mediaUrl")
    private String mediaUrl;

    @Column(name = "uploadTimestamp", nullable = false)
    private LocalDateTime uploadTimestamp;

    @Column(name = "fileSize")
    private Long fileSize;

    /**
     * Static factory method to create a new AchievementMedia instance
     * 
     * @param achievement the achievement to associate with this media
     * @param imageName the name of the uploaded image
     * @param contentType the MIME type of the image
     * @param imageData the binary data of the image
     * @return a new AchievementMedia instance
     */
    public static AchievementMedia create(Achievement achievement, String imageName, String contentType, byte[] imageData) {
        AchievementMedia media = new AchievementMedia();
        // media.setAchievement(achievement);
        media.setImageName(imageName);
        media.setContentType(contentType);
        media.setImageData(imageData);
        media.setUploadTimestamp(LocalDateTime.now());
        
        if (imageData != null) {
            media.setFileSize((long) imageData.length);
        }
        
        return media;
    }

}