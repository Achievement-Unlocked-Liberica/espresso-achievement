package espresso.challenge.domain.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import espresso.challenge.domain.events.ChallengeEvent;
import espresso.challenge.domain.events.ChallengeMediaEvent;
import espresso.challenge.domain.events.ChallengeCommentEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.models.DomainAggregate;
import espresso.common.domain.support.StringListConverter;
import espresso.user.domain.entities.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * Represents a challenge entity.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity(name = "Challenge")
@Table(name = "Challenges", indexes = {
        @Index(name = "idx_challenge_registered_at_desc", columnList = "registeredAt DESC"),
        @Index(name = "idx_challenge_id_pkey", columnList = "id", unique = true),
        @Index(name = "idx_challenge_entitykey_ukey", columnList = "entityKey", unique = true),
        @Index(name = "idx_challenge_userid_fkey", columnList = "userId", unique = false)
})
public class Challenge extends DomainAggregate {

    /**
     * The title or name of the challenge (maximum 200 characters).
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * A detailed description of what the challenge represents (maximum 1000 characters).
     */
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    /**
     * The date when this challenge will be fulfilled by the user.
     */
    @Column(name = "fulfillmentDate", nullable = false)
    private Date fulfillmentDate;

    /**
     * The user who created and owns this challenge.
     * Lazy-loaded to improve performance.
     */
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;

    /**
     * List of skill abbreviations associated with this challenge.
     * Stored as a comma-separated string in the database.
     */
    @Convert(converter = StringListConverter.class)
    private List<String> skills;

    /**
     * The visibility status of the challenge (PRIVATE, EVERYONE, etc.).
     */
    @Enumerated(EnumType.STRING)
    private ChallengeVisibilityStatus challengeVisibility;

    /**
     * List of media files (images) associated with this challenge.
     * Lazy-loaded to improve performance.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChallengeMedia> media;

    /**
     * List of comments associated with this challenge.
     * Lazy-loaded to improve performance.
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChallengeComment> comments;

    /*
     * The constructor is package-private to prevent the creation of a challenge
     * by external classes.
     * This is to ensure that the entity is created through the factory method.
     * 
     * @param initializeEntity Indicates whether the entity should be initialized.
     */
    protected Challenge(boolean initializeEntity) {
        if (initializeEntity) {
            this.initializeEntity();
        }
    }

    /**
     * Factory method to create a new challenge.
     * 
     * @param title           The title of the challenge
     * @param description     The description of the challenge
     * @param fulfillmentDate The date when the challenge will be fulfilled
     * @param isPublic        Whether the challenge is publicly visible
     * @param user            The user creating the challenge
     * @param skills          List of skill abbreviations associated with the challenge
     * @return A new Challenge entity
     */
    public static Challenge create(String title, String description, Date fulfillmentDate, boolean isPublic,
            User user, List<String> skills) {

        Challenge entity = new Challenge();

        entity.initializeEntity();

        entity.title = title;
        entity.description = description;
        entity.fulfillmentDate = fulfillmentDate;
        entity.challengeVisibility = isPublic
                ? ChallengeVisibilityStatus.EVERYONE
                : ChallengeVisibilityStatus.PRIVATE;

        entity.setUser(user);
        entity.setSkills(skills);

        entity.raiseChallengeCreated();

        return entity;
    }

    private void initializeEntity() {
        this.setEntityKey(espresso.common.domain.support.KeyGenerator.generateKey(7));
    }

    public boolean isCreator(User user) {
        return this.getUser().getEntityKey().equals(user.getEntityKey()) &&
               this.getUser().getId().equals(user.getId());
    }

    /**
     * Updates the challenge with new values for title, description, skills, and visibility.
     * This method allows updating the main properties of a challenge after it has been created.
     * 
     * @param title       The new title for the challenge
     * @param description The new description for the challenge
     * @param skills      List of skill abbreviations associated with the challenge
     * @param isPublic    Whether the challenge should be publicly visible
     */
    public void update(String title, String description, List<String> skills, boolean isPublic) {
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.challengeVisibility = isPublic
                ? ChallengeVisibilityStatus.EVERYONE
                : ChallengeVisibilityStatus.PRIVATE;

        this.updateEntity();

        this.raiseChallengeUpdated();
    }

    /**
     * Disables the challenge by setting the enabled property to false.
     * This removes the challenge from all filters, searches, and visibility without deleting it from the database.
     */
    public void disable() {
        this.setEnabled(false);

        this.updateEntity();

        this.raiseChallengeDisabled();
    }

    /**
     * Marks the challenge for deletion and raises a delete event.
     * This method should be called before the actual database deletion occurs.
     */
    public void delete() {
        this.updateEntity();

        this.raiseChallengeDeleted();
    }

    /**
     * Adds media to this challenge.
     * This method adds the media to the internal list and raises a media added event.
     * 
     * @param media The media being added to this challenge
     */
    public void addMedia(ChallengeMedia media) {
        if (this.media == null) {
            this.media = new ArrayList<>();
        }

        this.media.add(media);

        this.updateEntity();

        this.raiseMediaAdded(media);
    }

    /**
     * Adds a comment to this challenge.
     * This method adds the comment to the internal list and raises a comment added event.
     * 
     * @param comment The comment being added to this challenge
     */
    public void addComment(ChallengeComment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }

        this.comments.add(comment);

        this.updateEntity();

        this.raiseCommentAdded(comment);
    }

    // Converts this challenge to a KTO (Key Transfer Object) representation.
    public ChallengeKto toKto(){
        return new ChallengeKto() {
            @Override
            public Long getId() {
                return Challenge.this.getId();
            }
            
            @Override
            public String getEntityKey() {
                return Challenge.this.getEntityKey();
            }
        };
    }

    // #region Domain Events

    private void raiseChallengeCreated() {
        this.domainEvents.add(
                ChallengeEvent.create(
                        EventActionTypes.CREATED,
                        entityKey,
                        user.getEntityKey(),
                        title,
                        description,
                        fulfillmentDate,
                        skills.toArray(new String[0])));
    }

    private void raiseChallengeUpdated() {
        this.domainEvents.add(
                ChallengeEvent.create(
                        EventActionTypes.UPDATED,
                        entityKey,
                        user.getEntityKey(),
                        title,
                        description,
                        fulfillmentDate,
                        skills.toArray(new String[0])));
    }

    private void raiseChallengeDisabled() {
        this.domainEvents.add(
                ChallengeEvent.create(
                        EventActionTypes.DISABLED,
                        entityKey,
                        user.getEntityKey(),
                        title,
                        description,
                        fulfillmentDate,
                        skills.toArray(new String[0])));
    }

    private void raiseChallengeDeleted() {
        this.domainEvents.add(
                ChallengeEvent.create(
                        EventActionTypes.DELETED,
                        entityKey,
                        user.getEntityKey(),
                        title,
                        description,
                        fulfillmentDate,
                        skills.toArray(new String[0])));
    }

    private void raiseMediaAdded(ChallengeMedia media) {
        this.domainEvents.add(
                ChallengeMediaEvent.create(
                        EventActionTypes.CREATED,
                        media.getChallenge().getEntityKey(),
                        media.getChallenge().getUser().getEntityKey(),
                        media.getImageKey(),
                        media.getMediaUrl(),
                        media.getOriginalImageName(),
                        media.getContentType(),
                        media.getFileSize()));
    }

    private void raiseCommentAdded(ChallengeComment comment) {
        this.domainEvents.add(
                ChallengeCommentEvent.create(
                        EventActionTypes.CREATED,
                        comment.getChallenge().getEntityKey(),
                        comment.getUser().getEntityKey(),
                        comment.getText()));
    }

    // #endregion Domain Events
}
