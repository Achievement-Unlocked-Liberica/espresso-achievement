package espresso.user.domain.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;
import espresso.common.domain.models.DomainEntity;
import espresso.common.domain.support.KeyGenerator;

//TODO: Add the birthDate to the vertical feature
//TODO: Add the profilePictureUrl to the vertical feature

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "User")
@Table(name = "Users", indexes = {
        @Index(name = "user_entitykey_idx", columnList = "entitykey", unique = true),
        @Index(name = "user_username_idx", columnList = "username", unique = true),
        @Index(name = "user_email_idx", columnList = "email", unique = true)
})
public class User extends DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entitykey", nullable = false, unique = true)
    private String entityKey;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    // private String profilePictureUrl;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "profileImageId", referencedColumnName = "id")
    UserProfileImage profileImage;

    private boolean emailVerified;
    private boolean ageVerified;
    private boolean phoneVerified;
    private boolean addressVerified;

    private boolean active;
    private OffsetDateTime registeredAt;

    public static User create(String username, String email, String firstName, String lastName,
            LocalDate birthDate) {
        User entity = new User();

        entity.setEntityKey(KeyGenerator.generateKey(7));

        entity.setUsername(username);
        entity.setEmail(email);
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setBirthDate(birthDate);

        entity.registeredAt = OffsetDateTime.now(ZoneOffset.UTC);
        entity.active = true;
        entity.emailVerified = false;

        return entity;
    }

    /**
     * Updates the basic profile information of the registered user
     * 
     * @param username  The new username (can be null to skip update)
     * @param email     The new email (can be null to skip update)
     * @param firstName The new first name (can be null to skip update)
     * @param lastName  The new last name (can be null to skip update)
     * @param birthDate The new birth date (can be null to skip update)
     */
    public void setBasicProfileInfo(String username, String email, String firstName, String lastName,
            LocalDate birthDate) {
        if (username != null) {
            this.username = username;
        }

        if (email != null) {
            this.email = email;
        }

        if (firstName != null) {
            this.firstName = firstName;
        }

        if (lastName != null) {
            this.lastName = lastName;
        }

        if (birthDate != null) {
            this.birthDate = birthDate;
        }
    }

}