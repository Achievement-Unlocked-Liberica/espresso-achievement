package espresso.achievement.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.ApiMessageHelper;
import espresso.achievement.domain.contracts.IAchievementQueryHandler;
import espresso.achievement.domain.queries.GetAchievementDetailQuery;
import espresso.achievement.domain.queries.GetLatestAchievementsQuery;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonQryApi;
import espresso.common.service.operational.ApiLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * REST API controller for handling achievement query operations.
 * Provides endpoints for retrieving achievement data including latest achievements
 * and detailed achievement information. All endpoints support size-based DTOs
 * for optimized data transfer.
 */
@RestController("Achievement Qry Api")
@RequestMapping("/api/qry/achievement")
public class AchievementQryApi extends CommonQryApi {

	private final IAchievementQueryHandler achievementQueryHandler;

	/**
	 * Constructor for dependency injection.
	 * 
	 * @param messageHelper Helper for API message handling
	 * @param achievementQueryHandler Handler for processing achievement query operations
	 */
	public AchievementQryApi(
			ApiMessageHelper messageHelper,
			IAchievementQueryHandler achievementQueryHandler) {
		super(messageHelper);
		this.achievementQueryHandler = achievementQueryHandler;
	}

	@Operation(summary = "Get Latest Achievements", description = "Retrieves the latest achievements ordered by completion date (newest first).")
	@GetMapping("/latest")
	@ApiResponse(responseCode = "200:OK", description = "Returns the latest achievements in the specified DTO format.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access to the achievement service.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Achievement not found.")
	@ApiLogger("Get latest achievements")
	public ResponseEntity<ServiceResponse<Object>> getLatestAchievements(GetLatestAchievementsQuery qry) {

		return executeQuery(qry, achievementQueryHandler::handle);
	}

	@Operation(summary = "Get Achievement Detail", description = "Retrieves the details for a single achievment using the given size.")
	@GetMapping("/detail")
	@ApiResponse(responseCode = "200:OK", description = "Returns the latest achievements in the specified DTO format.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access to the achievement service.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Achievement not found.")
	@ApiLogger("Get achievement detail")
	public ResponseEntity<ServiceResponse<Object>> getAchievementDetail(GetAchievementDetailQuery qry) {

		return executeQuery(qry, achievementQueryHandler::handle);
	}

}
