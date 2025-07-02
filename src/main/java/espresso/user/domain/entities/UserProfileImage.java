package espresso.user.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import espresso.common.domain.models.ValueEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UserProfileImage")
@Table(name = "UserProfileImages", indexes = {
        @Index(name = "user_profile_image_user_idx", columnList = "userId", unique = true)
})
public class UserProfileImage extends ValueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @OneToOne(mappedBy = "profileImage")
    private User user;

    private String imageName;

    private String contentType;

    private String imageExtension;

    private String profileImageUrl;

    // @Lob
    // @Column(name = "imageData", columnDefinition = "BYTEA")
    @Transient
    private byte[] imageData;

    public static UserProfileImage create(User user, String imageName, String contentType, byte[] imageData) {
        UserProfileImage entity = new UserProfileImage();
        entity.setUser(user);
        entity.setImageName(imageName);
        entity.setImageExtension(imageName.substring(imageName.lastIndexOf('.') + 1));
        entity.setContentType(contentType);
        entity.setImageData(imageData);

        return entity;
    }
}
