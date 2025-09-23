package espresso.user.domain.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import espresso.common.domain.models.DomainEntity;
import espresso.common.domain.support.KeyGenerator;
import espresso.common.domain.support.NameGenerator;
import espresso.common.domain.support.PasswordService;

//TODO: Add the birthDate to the vertical feature
//TODO: Add the profilePictureUrl to the vertical feature

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity(name = "User")
@Table(name = "Users", indexes = {
        @Index(name = "idx_user_id_pkey", columnList = "id", unique = true),
        @Index(name = "idx_user_entitykey_ukey", columnList = "entitykey", unique = true),
        @Index(name = "idx_user_username_ukey", columnList = "username", unique = true),
        @Index(name = "idx_user_email_ukey", columnList = "email", unique = true)
})
public class User extends DomainEntity {

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "passwordHash", nullable = false)
    private String passwordHash;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    @JsonManagedReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileImageId", referencedColumnName = "id")
    private UserProfileImage profileImage;

    private boolean emailVerified = false;
    private boolean ageVerified = false;
    private boolean phoneVerified = false;
    private boolean addressVerified = false;

    public static User create(String username, String email, String password, String firstName, String lastName,
            LocalDate birthDate) {
        User entity = new User();

        entity.setEntityKey(KeyGenerator.generateKey(7));

        entity.setUsername(username);
        entity.setEmail(email);
        entity.setPasswordHash(PasswordService.hashPassword(password));
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setBirthDate(birthDate);

        return entity;
    }

    /**
     * Creates a new User entity for registration with basic information only
     * 
     * @param username The username for the new user
     * @param email    The email address for the new user
     * @param password The plain text password (will be hashed)
     * @return A new User entity ready for registration
     */
    public static User createForRegistration(String username, String email, String password) {
        User entity = new User();

        entity.setEntityKey(KeyGenerator.generateKey(7));
        entity.setUsername(username);
        entity.setEmail(email);
        entity.setPasswordHash(PasswordService.hashPassword(password));

        String randomName = NameGenerator.generateProfileName();

        if (randomName != null && !randomName.isEmpty()) {
            entity.firstName = randomName.split(" ")[0];
            entity.lastName = randomName.split(" ")[1];
        }

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

    /**
     * Verifies a plain text password against the stored hash
     * 
     * @param plainPassword The plain text password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword) {
        return PasswordService.verifyPassword(plainPassword, this.passwordHash);
    }

    /**
     * Updates the user's password with a new hashed password
     * 
     * @param newPassword The new plain text password
     */
    public void updatePassword(String newPassword) {
        this.passwordHash = PasswordService.hashPassword(newPassword);
    }

    /**
     * Creates a User entity from a UserKto (Data Transfer Object)
     * 
     * @param kto
     */
    public static User fromKto(UserKto kto) {
        return User.builder()
            .id(kto.getId())
            .entityKey(kto.getEntityKey())
            .build();
    }
}