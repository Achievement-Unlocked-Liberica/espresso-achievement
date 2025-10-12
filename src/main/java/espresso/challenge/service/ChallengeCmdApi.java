package espresso.challenge.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import espresso.ApiMessageHelper;
import espresso.challenge.domain.commands.CreateChallengeCommand;
import espresso.challenge.domain.commands.UpdateChallengeCommand;
import espresso.challenge.domain.contracts.ICreateChallengeCommandHandler;
import espresso.challenge.domain.contracts.IUpdateChallengeCommandHandler;
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
	private final IUpdateChallengeCommandHandler updateChallengeCommandHandler;

	/**
	 * Constructor for dependency injection.
	 * 
	 * @param messageHelper Helper for API message handling
	 * @param createChallengeCommandHandler Handler for processing challenge creation commands
	 * @param updateChallengeCommandHandler Handler for processing challenge update commands
	 */
	public ChallengeCmdApi(
			ApiMessageHelper messageHelper,
			ICreateChallengeCommandHandler createChallengeCommandHandler,
			IUpdateChallengeCommandHandler updateChallengeCommandHandler) {
		super(messageHelper);
		this.createChallengeCommandHandler = createChallengeCommandHandler;
		this.updateChallengeCommandHandler = updateChallengeCommandHandler;
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
}
