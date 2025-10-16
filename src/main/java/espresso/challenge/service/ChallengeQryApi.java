package espresso.challenge.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.ApiMessageHelper;
import espresso.challenge.domain.contracts.IChallengeQueryHandler;
import espresso.challenge.domain.queries.GetChallengeDetailQuery;
import espresso.challenge.domain.queries.GetLatestChallengesQuery;
import espresso.challenge.domain.queries.GetMyChallengesQuery;
import espresso.challenge.domain.queries.GetUserChallengesQuery;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonQryApi;
import espresso.common.service.operational.ApiLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * REST API controller for handling challenge query operations.
 * Provides endpoints for retrieving challenge data including latest
 * challenges and detailed challenge information. All endpoints support size-based DTOs
 * for optimized data transfer.
 */
@RestController("Challenge Qry Api")
@RequestMapping("/api/qry/challenge")
public class ChallengeQryApi extends CommonQryApi {

	private final IChallengeQueryHandler challengeQueryHandler;

	/**
	 * Constructor for dependency injection.
	 * 
	 * @param messageHelper           Helper for API message handling
	 * @param challengeQueryHandler Handler for processing challenge query operations
	 */
	public ChallengeQryApi(
			ApiMessageHelper messageHelper,
			IChallengeQueryHandler challengeQueryHandler) {
		super(messageHelper);
		this.challengeQueryHandler = challengeQueryHandler;
	}

	@Operation(summary = "Get Latest Challenges", description = "Retrieves the latest challenges ordered by registration date (newest first).")
	@GetMapping("/latest")
	@ApiResponse(responseCode = "200:OK", description = "Returns the latest challenges in the specified DTO format.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access to the challenge service.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Challenge not found.")
	@ApiLogger("Get latest challenges")
	public ResponseEntity<ServiceResponse<Object>> getLatestChallenges(GetLatestChallengesQuery qry) {

		return executeQuery(qry, challengeQueryHandler::handle);
	}

	@Operation(summary = "Get Challenge Detail", description = "Retrieves the details for a single challenge using the given size.")
	@GetMapping("/detail")
	@ApiResponse(responseCode = "200:OK", description = "Returns the challenge detail in the specified DTO format.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access to the challenge service.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Challenge not found.")
	@ApiLogger("Get challenge detail")
	public ResponseEntity<ServiceResponse<Object>> getChallengeDetail(GetChallengeDetailQuery qry) {

		return executeQuery(qry, challengeQueryHandler::handle);
	}

	@Operation(summary = "Get My Challenges", description = "Retrieves challenges for the authenticated user ordered by registration date (newest first).")
	@GetMapping("/my")
	@ApiResponse(responseCode = "200:OK", description = "Returns the authenticated user's challenges in the specified DTO format.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access - invalid or missing JWT token.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "No challenges found for the authenticated user.")
	@ApiLogger("Get my challenges")
	public ResponseEntity<ServiceResponse<Object>> getMyChallenges(GetMyChallengesQuery qry) {

		String userKey = getAuthenticatedUserKey();

		qry.setUserKey(userKey);

		return executeQuery(qry, challengeQueryHandler::handle);
	}

	@Operation(summary = "Get User Challenges", description = "Retrieves challenges for a specific user ordered by registration date (newest first).")
	@GetMapping("/user/{requestedUserKey}")
	@ApiResponse(responseCode = "200:OK", description = "Returns the specified user's challenges in the specified DTO format.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Invalid user key format.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access to the challenge service.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "No challenges found for the specified user.")
	@ApiLogger("Get user challenges")
	public ResponseEntity<ServiceResponse<Object>> getUserChallenges(
			@PathVariable String requestedUserKey,
			GetUserChallengesQuery qry) {

		String userKey = getAuthenticatedUserKey();

		qry.setUserKey(userKey);
		qry.setRequestedUserKey(requestedUserKey);

		return executeQuery(qry, challengeQueryHandler::handle);
	}
}
