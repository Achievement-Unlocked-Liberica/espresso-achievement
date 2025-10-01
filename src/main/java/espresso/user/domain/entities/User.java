package espresso.user.domain.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import espresso.common.domain.models.DomainEntity;
import espresso.common.domain.support.KeyGenerator;
import espresso.common.domain.support.NameGenerator;
import espresso.common.domain.support.PasswordService;

/**
 * Represents a user entity in the achievement system.
 * Contains user authentication information, profile data, and verification status.
 * Users can create achievements, comment on achievements, and celebrate others' accomplishments.
 * Supports secure password hashing and various verification workflows.
 */
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

    /**
     * Unique username for the user account.
     * Used for authentication and public identification.
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * User's email address for authentication and notifications.
     * Must be unique across the system.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Hashed password for secure authentication.
     * Never store or transmit plain text passwords.
     */
    @Column(name = "passwordHash", nullable = false)
    private String passwordHash;

    /**
     * User's first name for personalization.
     */
    private String firstName;

    /**
     * User's last name for personalization.
     */
    private String lastName;
    
    /**
     * User's birth date for age verification and personalization.
     */
    private LocalDate birthDate;

    /**
     * Optional profile image associated with the user.
     * Lazy-loaded to improve performance.
     */
    @JsonManagedReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileImageId", referencedColumnName = "id")
    private UserProfileImage profileImage;

    /**
     * Indicates whether the user's email address has been verified.
     */
    @Default
    private boolean emailVerified = false;
    
    /**
     * Indicates whether the user's age has been verified.
     */
    @Default
    private boolean ageVerified = false;
    
    /**
     * Indicates whether the user's phone number has been verified.
     */
    @Default
    private boolean phoneVerified = false;
    
    /**
     * Indicates whether the user's address has been verified.
     */
    @Default
    private boolean addressVerified = false;

    /**
     * Creates a new User entity with complete profile information.
     * 
     * @param username  The unique username for the user
     * @param email     The user's email address
     * @param password  The plain text password (will be hashed)
     * @param firstName The user's first name
     * @param lastName  The user's last name
     * @param birthDate The user's birth date
     * @return A new User entity ready for persistence
     */
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
     * Creates a User entity from a UserKto (Key Transfer Object).
     * Used when only basic identification is needed without full user data.
     * 
     * @param kto The UserKto containing ID and entity key
     * @return A User entity with minimal data for references
     */
    public static User fromKto(UserKto kto) {
        return User.builder()
            .id(kto.getId())
            .entityKey(kto.getEntityKey())
            .build();
    }
}