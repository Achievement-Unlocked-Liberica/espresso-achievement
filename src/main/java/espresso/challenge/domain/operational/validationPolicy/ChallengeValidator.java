package espresso.challenge.domain.operational.validationPolicy;

import espresso.challenge.domain.entities.Challenge;
import espresso.challenge.domain.entities.ChallengeMedia;
import espresso.challenge.domain.operational.exceptionPolicy.ChallengeException;

/**
 * Validator for Challenge entity operations.
 * Provides validation logic for challenge persistence and updates.
 */
public class ChallengeValidator {

    /**
     * Private constructor to prevent instantiation.
     */
    private ChallengeValidator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Validates a challenge entity before persisting to the database.
     * 
     * @param challenge The challenge to validate
     * @throws ChallengeException if validation fails
     */
    public static void validateForPersistence(Challenge challenge) {
        if (challenge == null) {
            throw ChallengeException.invalidChallenge("Challenge cannot be null");
        }

        if (challenge.getEntityKey() == null || challenge.getEntityKey().trim().isEmpty()) {
            throw ChallengeException.invalidChallenge("Challenge entity key cannot be null or empty");
        }

        if (challenge.getTitle() == null || challenge.getTitle().trim().isEmpty()) {
            throw ChallengeException.invalidChallenge("Challenge title cannot be null or empty");
        }

        if (challenge.getDescription() == null || challenge.getDescription().trim().isEmpty()) {
            throw ChallengeException.invalidChallenge("Challenge description cannot be null or empty");
        }

        if (challenge.getUser() == null) {
            throw ChallengeException.invalidChallenge("Challenge must have an associated user");
        }

        if (challenge.getFulfillmentDate() == null) {
            throw ChallengeException.invalidChallenge("Challenge fulfillment date cannot be null");
        }
    }

    /**
     * Validates a challenge entity before updating in the database.
     * 
     * @param challenge The challenge to validate
     * @throws ChallengeException if validation fails
     */
    public static void validateForUpdate(Challenge challenge) {
        validateForPersistence(challenge);

        if (challenge.getId() == null) {
            throw ChallengeException.invalidChallenge("Challenge ID cannot be null for update operation");
        }
    }

    /**
     * Validates a ChallengeMedia entity for persistence operations.
     * Checks that the media and its associated challenge are not null.
     *
     * @param challengeMedia The challenge media to validate
     * @throws ChallengeException if validation fails
     */
    public static void validateChallengeMedia(ChallengeMedia challengeMedia) {
        if (challengeMedia == null) {
            throw ChallengeException.invalidChallenge("Challenge media cannot be null");
        }
        
        Challenge challenge = challengeMedia.getChallenge();
        if (challenge == null) {
            throw ChallengeException.invalidChallenge("Challenge in challenge media cannot be null");
        }
    }

    /**
     * Validates ChallengeMedia for S3 upload operations.
     * Checks media entity and image data.
     *
     * @param challengeMedia The challenge media to validate
     * @throws ChallengeException if validation fails
     */
    public static void validateChallengeMediaForUpload(ChallengeMedia challengeMedia) {
        if (challengeMedia == null) {
            throw ChallengeException.invalidChallenge("Challenge media cannot be null");
        }
        
        if (challengeMedia.getImageData() == null || challengeMedia.getImageData().length == 0) {
            throw ChallengeException.invalidChallenge("Challenge media image data cannot be null or empty");
        }
    }
}
