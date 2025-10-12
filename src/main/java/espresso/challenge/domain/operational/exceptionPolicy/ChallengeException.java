package espresso.challenge.domain.operational.exceptionPolicy;

import espresso.common.infrastructure.correlation.CorrelationContext;

/**
 * Custom exception for challenge-related operations.
 * Provides domain-specific exception handling with correlation tracking.
 */
public class ChallengeException extends RuntimeException {

    private final String errorCode;
    private final String scenario;
    private final String correlationId;

    /**
     * Private constructor to enforce factory method usage.
     */
    private ChallengeException(String message, String errorCode, String scenario) {
        super(message);
        this.errorCode = errorCode;
        this.scenario = scenario;
        this.correlationId = CorrelationContext.getCorrelationId();
    }

    /**
     * Gets the error code.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the scenario description.
     */
    public String getScenario() {
        return scenario;
    }

    /**
     * Gets the correlation ID.
     */
    public String getCorrelationId() {
        return correlationId;
    }

    // Factory methods for different exception scenarios

    public static ChallengeException creationFailed(String reason) {
        return new ChallengeException(
            "Challenge creation failed: " + reason,
            "CHALLENGE_CREATION_FAILED",
            "create_challenge"
        );
    }

    public static ChallengeException updateFailed(String challengeKey, String reason) {
        return new ChallengeException(
            "Challenge update failed for key " + challengeKey + ": " + reason,
            "CHALLENGE_UPDATE_FAILED",
            "update_challenge"
        );
    }

    public static ChallengeException notFound(String challengeKey) {
        return new ChallengeException(
            "Challenge not found with key: " + challengeKey,
            "CHALLENGE_NOT_FOUND",
            "find_challenge"
        );
    }

    public static ChallengeException deletionFailed(String challengeKey, String reason) {
        return new ChallengeException(
            "Challenge deletion failed for key " + challengeKey + ": " + reason,
            "CHALLENGE_DELETION_FAILED",
            "delete_challenge"
        );
    }

    public static ChallengeException invalidChallenge(String reason) {
        return new ChallengeException(
            "Invalid challenge: " + reason,
            "INVALID_CHALLENGE",
            "validate_challenge"
        );
    }
}
