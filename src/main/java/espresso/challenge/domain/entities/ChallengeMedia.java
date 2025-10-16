package espresso.challenge.domain.entities;

import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


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

/**
 * Represents media files (images) associated with a challenge.
 * This entity stores metadata and references to images uploaded by users to showcase their challenges.
 * The actual image data is stored externally (e.g., S3) and referenced via mediaUrl.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ChallengeMedia")
@Table(name = "ChallengeMedias")
public class ChallengeMedia extends ValueEntity {

    /**
     * Unique identifier for the media record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique key generated for the image, combining challenge key and random key.
     */
    @Column(name = "imageKey")
    private String imageKey;

    /**
     * The challenge that this media belongs to.
     * Lazy-loaded to improve performance.
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challengeId", referencedColumnName = "id")
    private Challenge challenge;

    /**
     * The stored filename of the image in the storage system.
     */
    @Column(name = "imageName", nullable = false)
    private String imageName;

    /**
     * The original filename of the uploaded image as provided by the user.
     */
    @Column(name = "originalImageName", nullable = false)
    private String originalImageName;

    /**
     * The MIME content type of the image (e.g., image/jpeg, image/png).
     */
    @Column(name = "contentType")
    private String contentType;

    /**
     * The binary data of the image. Currently stored as transient and not persisted to database.
     * Image data is stored externally and accessed via mediaUrl.
     */
    // @Lob
    // @Column(name = "imageData")
    @Transient
    private byte[] imageData;

    /**
     * The URL where the image can be accessed from external storage.
     */
    @Column(name = "mediaUrl")
    private String mediaUrl;

    /**
     * The timestamp when the image was uploaded to the system.
     */
    @Column(name = "uploadTimestamp", nullable = false)
    private LocalDateTime uploadTimestamp;

    /**
     * The size of the image file in bytes.
     */
    @Column(name = "fileSize")
    private Long fileSize;

    /**
     * Static factory method to create a new ChallengeMedia instance
     * 
     * @param challenge the challenge to associate with this media
     * @param imageName   the name of the uploaded image
     * @param contentType the MIME type of the image
     * @param imageData   the binary data of the image
     * @return a new ChallengeMedia instance
     */
    public static ChallengeMedia create(Challenge challenge, String originalImageName, String contentType,
            byte[] imageData) {
        ChallengeMedia media = new ChallengeMedia();

        String imageExtension = null;
        if (originalImageName != null && originalImageName.contains(".")) {
            imageExtension = originalImageName.substring(originalImageName.lastIndexOf('.') -1);
        }

        String nameKey = KeyGenerator.generateKey(7);

        media.setImageKey(challenge.getEntityKey() + "-" + nameKey);
        media.setChallenge(challenge);
        media.setImageName(nameKey + imageExtension);
        media.setOriginalImageName(originalImageName);
        media.setContentType(contentType);
        media.setImageData(imageData);
        media.setUploadTimestamp(LocalDateTime.now());

        if (imageData != null) {
            media.setFileSize((long) imageData.length);
        }

        return media;
    }


    public ChallengeMediaKto toKto(){
        return new ChallengeMediaKto() {
            @Override
            public Long getId() {
                return ChallengeMedia.this.getId();
            }
            
            @Override
            public String getImageKey() {
                return ChallengeMedia.this.getImageKey();
            }

            @Override
            public String getMediaUrl(){
                return ChallengeMedia.this.getMediaUrl();
            }
        };
    }
}
