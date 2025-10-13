package espresso.challenge.domain.contracts;

import espresso.challenge.domain.commands.UploadChallengeMediaCommand;
import espresso.common.domain.responses.HandlerResponse;

/**
 * Interface for handling the upload of media files to a challenge.
 */
public interface IUploadChallengeMediaCommandHandler {
    
    /**
     * Handles the upload of media files to an existing challenge.
     * 
     * @param cmd The command containing the challenge key, user key, and image files
     * @return HandlerResponse containing the challenge with updated media or error details
     */
    HandlerResponse<Object> handle(UploadChallengeMediaCommand cmd);
}
