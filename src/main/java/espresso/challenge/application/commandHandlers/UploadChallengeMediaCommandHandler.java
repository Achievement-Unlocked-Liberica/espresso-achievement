package espresso.challenge.application.commandHandlers;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import espresso.challenge.domain.contracts.IUploadChallengeMediaCommandHandler;
import espresso.challenge.domain.commands.UploadChallengeMediaCommand;
import espresso.challenge.domain.contracts.IChallengeRepository;
import espresso.challenge.domain.contracts.IChallengeMediaRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.user.domain.entities.UserKto;
import espresso.challenge.domain.entities.Challenge;
import espresso.challenge.domain.entities.ChallengeMedia;
import espresso.common.application.handlers.CommonCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.challenge.domain.operational.exceptionPolicy.ChallengeHandlerExceptionPolicy;

import lombok.extern.slf4j.Slf4j;
// Validation centralized in CommonCommandHandler

/**
 * Handles the upload of media files for a challenge.
 */
@Slf4j
@Service
public class UploadChallengeMediaCommandHandler extends CommonCommandHandler
        implements IUploadChallengeMediaCommandHandler {

    private final IChallengeRepository challengeRepository;
    private final IChallengeMediaRepository challengeMediaRepository;
    private final IUserRepository userRepository;
    private final ChallengeHandlerExceptionPolicy exceptionPolicy;

    /**
     * Constructor for dependency injection.
     * 
     * @param challengeRepository Repository for challenge entity persistence operations
     * @param challengeMediaRepository Repository for challenge media operations
     * @param userRepository Repository for user entity queries and operations
     * @param exceptionPolicy Centralized exception handling policy
     */
    public UploadChallengeMediaCommandHandler(
            IChallengeRepository challengeRepository,
            IChallengeMediaRepository challengeMediaRepository,
            IUserRepository userRepository,
            ChallengeHandlerExceptionPolicy exceptionPolicy) {
        this.challengeRepository = challengeRepository;
        this.challengeMediaRepository = challengeMediaRepository;
        this.userRepository = userRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    public HandlerResponse<Object> handle(UploadChallengeMediaCommand cmd) {
        try {
            // Validate the command using shared Validator and command-specific checks
            var validationResult = validateCommand(cmd);
            if (validationResult != null)
                return validationResult;

            // Get the profile of the user that is uploading the media
            // We use a Kto instance since we just need to know if it exists and its keys,
            UserKto userKto = userRepository.findByKey(cmd.getUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Get the challenge by key
            Challenge challenge = challengeRepository.getChallengeByKey(Challenge.class,
                    cmd.getChallengeKey());

            if (challenge == null) {
                return HandlerResponse.error("Challenge not found", ResponseType.NOT_FOUND);
            }

            // Verify that the user is authorized to upload media for this challenge (user must own the challenge)
            if (!challenge.isCreator(User.fromKto(userKto))) {
                return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO UPLOAD MEDIA FOR THIS CHALLENGE",
                        ResponseType.UNAUTHORIZED);
            }

            // Process each image in the array
            for (MultipartFile image : cmd.getImages()) {

                // Create ChallengeMedia entity
                ChallengeMedia media = ChallengeMedia.create(
                        challenge,
                        image.getOriginalFilename(),
                        image.getContentType(),
                        image.getBytes());

                // Save the media
                ChallengeMedia savedMedia = challengeMediaRepository.save(challenge, media);

                // Add media to challenge (this will raise domain events)
                challenge.addMedia(savedMedia);
            }

            this.publishDomainEvents(challenge);

            return HandlerResponse.success(challenge.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "upload media to challenge");
        }
    }
}
