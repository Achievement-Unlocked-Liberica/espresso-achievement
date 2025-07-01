package espresso.user.domain.entities;

import espresso.common.domain.models.ValueEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "UserProfileImage")
@Table(name = "UserProfileImages")
public class UserProfileImage extends ValueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    private String imageName;

    private String contentType;

    private String imageExtension;

    @Lob
    @Column(name = "imageData", columnDefinition = "BYTEA")
    private byte[] imageData;

    private String profileImageUrl;

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

