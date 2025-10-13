package espresso.challenge.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import espresso.ApiMessageHelper;
import espresso.challenge.domain.commands.CreateChallengeCommand;
import espresso.challenge.domain.commands.UpdateChallengeCommand;
import espresso.challenge.domain.commands.DisableChallengeCommand;
import espresso.challenge.domain.commands.DeleteChallengeCommand;
import espresso.challenge.domain.commands.UploadChallengeMediaCommand;
import espresso.challenge.domain.commands.AddChallengeCommentCommand;
import espresso.challenge.domain.commandhandlers.IAddChallengeCommentCommandHandler;
import espresso.challenge.domain.entities.ChallengeComment;
import espresso.challenge.domain.contracts.ICreateChallengeCommandHandler;
import espresso.challenge.domain.contracts.IUpdateChallengeCommandHandler;
import espresso.challenge.domain.contracts.IDisableChallengeCommandHandler;
import espresso.challenge.domain.contracts.IDeleteChallengeCommandHandler;
import espresso.challenge.domain.contracts.IUploadChallengeMediaCommandHandler;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonCmdApi;
import espresso.common.service.operational.ApiLogger;

/**
 * REST API controller for handling challenge command operations.
 * Provides endpoints for creating, updating, deleting, and managing challenges.
 * All endpoints require proper authentication and authorization.
 */
@RestController("challengeCmdApi")
@RequestMapping("/api/cmd/challenge")
@Tag(name = "Challenge Command API", description = "API for handling Challenge commands.")
public class ChallengeCmdApi extends CommonCmdApi {

	private final ICreateChallengeCommandHandler createChallengeCommandHandler;
	private final IUploadChallengeMediaCommandHandler uploadChallengeMediaCommandHandler;
	private final IAddChallengeCommentCommandHandler addChallengeCommentCommandHandler;
	private final IUpdateChallengeCommandHandler updateChallengeCommandHandler;
	private final IDisableChallengeCommandHandler disableChallengeCommandHandler;
	private final IDeleteChallengeCommandHandler deleteChallengeCommandHandler;

	/**
	 * Constructor for dependency injection.
	 * 
	 * @param messageHelper Helper for API message handling
	 * @param createChallengeCommandHandler Handler for processing challenge creation commands
	 * @param uploadChallengeMediaCommandHandler Handler for processing challenge media upload commands
	 * @param addChallengeCommentCommandHandler Handler for processing challenge comment addition commands
	 * @param updateChallengeCommandHandler Handler for processing challenge update commands
	 * @param disableChallengeCommandHandler Handler for processing challenge disable commands
	 * @param deleteChallengeCommandHandler Handler for processing challenge deletion commands
	 */
	public ChallengeCmdApi(
			ApiMessageHelper messageHelper,
			ICreateChallengeCommandHandler createChallengeCommandHandler,
			IUploadChallengeMediaCommandHandler uploadChallengeMediaCommandHandler,
			IAddChallengeCommentCommandHandler addChallengeCommentCommandHandler,
			IUpdateChallengeCommandHandler updateChallengeCommandHandler,
			IDisableChallengeCommandHandler disableChallengeCommandHandler,
			IDeleteChallengeCommandHandler deleteChallengeCommandHandler) {
		super(messageHelper);
		this.createChallengeCommandHandler = createChallengeCommandHandler;
		this.uploadChallengeMediaCommandHandler = uploadChallengeMediaCommandHandler;
		this.addChallengeCommentCommandHandler = addChallengeCommentCommandHandler;
		this.updateChallengeCommandHandler = updateChallengeCommandHandler;
		this.disableChallengeCommandHandler = disableChallengeCommandHandler;
		this.deleteChallengeCommandHandler = deleteChallengeCommandHandler;
	}

	@Operation(summary = "Create New Challenge", description = "Creates a new Challenge from the provided command.")
	@PostMapping("")
	@ApiResponse(responseCode = "201:CREATED", description = "Created a new Challenge successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Create new challenge")
	public ResponseEntity<ServiceResponse<Object>> createChallenge(@RequestBody CreateChallengeCommand command) {

		String userKey = getAuthenticatedUserKey();

		command.setUserKey(userKey);

		return executeCommand(command, createChallengeCommandHandler::handle);
	}

	@Operation(summary = "Upload Challenge Media", description = "Uploads media files for an existing Challenge.")
	@PostMapping("/{key}/media")
	@ApiResponse(responseCode = "201:CREATED", description = "Media uploaded successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Challenge not found.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "User not authorized to upload media for this challenge.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Upload challenge media")
	public ResponseEntity<ServiceResponse<Object>> uploadChallengeMedia(
			@RequestParam("images") MultipartFile[] images,
			@PathVariable String key) {

		String userKey = getAuthenticatedUserKey();

		UploadChallengeMediaCommand command = new UploadChallengeMediaCommand(key, userKey, images);

		return executeCommand(command, uploadChallengeMediaCommandHandler::handle);
	}

	@Operation(summary = "Add Comment to Challenge", description = "Adds a comment to an existing Challenge.")
	@PostMapping("/{key}/comments")
	@ApiResponse(responseCode = "201:CREATED", description = "Comment added successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Challenge or user not found.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "User not authorized.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Add comment to challenge")
	public ResponseEntity<ServiceResponse<Object>> addComment(
			@PathVariable String key,
			@RequestBody AddChallengeCommentCommand command) {

		String userKey = getAuthenticatedUserKey();

		command.withUserKey(userKey).withChallengeKey(key);

		ChallengeComment comment = addChallengeCommentCommandHandler.execute(command);

		return ResponseEntity.status(201)
				.body(ServiceResponse.success(org.springframework.http.HttpStatus.CREATED, comment, null));
	}

	@Operation(summary = "Update Challenge", description = "Updates an existing Challenge with the provided data.")
	@PutMapping("/{key}")
	@ApiResponse(responseCode = "200:OK", description = "Challenge updated successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "User is not authorized to update this challenge.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Challenge not found.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Update challenge")
	public ResponseEntity<ServiceResponse<Object>> updateChallenge(
			@PathVariable("key") String challengeKey,
			@RequestBody UpdateChallengeCommand command) {

		String userKey = getAuthenticatedUserKey();

		command.setUserKey(userKey);
		command.setChallengeKey(challengeKey);

		return executeCommand(command, updateChallengeCommandHandler::handle);
	}

	/**
	 * Disables an existing challenge by setting its enabled property to false.
	 * This removes the challenge from all filters, searches, and visibility without deleting it from the database.
	 * The userKey is automatically extracted from the JWT authentication token.
	 * 
	 * @param key The 7-character alphanumeric key of the challenge to disable
	 * @return ResponseEntity with the disabled challenge or error response
	 */
	@Operation(summary = "Disable Challenge", description = "Disables a challenge by setting its enabled property to false.")
	@PatchMapping("/{key}/disable")
	@ApiResponse(responseCode = "200:OK", description = "Challenge disabled successfully.")
	@ApiResponse(responseCode = "204:NO_CONTENT", description = "No action taken because the challenge was already disabled.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access - invalid or missing JWT token or user not authorized to disable this challenge.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Challenge or user not found.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Disable challenge")
	public ResponseEntity<ServiceResponse<Object>> disableChallenge(@PathVariable String key) {
		String userKey = getAuthenticatedUserKey();

		DisableChallengeCommand command = new DisableChallengeCommand(key, userKey);

		return executeCommand(command, disableChallengeCommandHandler::handle);
	}

	/**
	 * Deletes an existing challenge by permanently removing it and all associated data from the database.
	 * This removes the challenge from all filters, searches, and visibility and cannot be undone.
	 * The userKey is automatically extracted from the JWT authentication token.
	 * All dependencies (comments and media) are deleted in proper order to maintain referential integrity.
	 * 
	 * @param key The 7-character alphanumeric key of the challenge to delete
	 * @return ResponseEntity with success confirmation or error response
	 */
	@Operation(summary = "Delete Challenge", description = "Permanently deletes a challenge and all associated data from the database.")
	@DeleteMapping("/{key}")
	@ApiResponse(responseCode = "200:OK", description = "Challenge deleted successfully.")
	@ApiResponse(responseCode = "204:NO_CONTENT", description = "No action taken because the challenge didn't exist and was not deleted.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access - invalid or missing JWT token or user not authorized to delete this challenge.")
	@ApiResponse(responseCode = "403:FORBIDDEN", description = "User lacks authorization to delete this challenge.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Challenge or user not found.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred during deletion.")
	@ApiLogger("Delete challenge")
	public ResponseEntity<ServiceResponse<Object>> deleteChallenge(@PathVariable String key) {
		String userKey = getAuthenticatedUserKey();

		DeleteChallengeCommand command = new DeleteChallengeCommand(key, userKey);

		return executeCommand(command, deleteChallengeCommandHandler::handle);
	}
}
