package espresso.achievement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.achievement.domain.contracts.IAchievementQueryHandler;
import espresso.achievement.domain.queries.GetAchievementDetailQuery;
import espresso.achievement.domain.queries.GetLatestAchievementsQuery;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonQryApi;
import espresso.common.service.operational.ApiLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController("Achievement Qry Api")
@RequestMapping("/api/qry/achievement")
public class AchievementQryApi extends CommonQryApi {

	/*
	 * @Autowired
	 * private ApiMessageHelper apiMsgHelper;
	 * 
	 * return ResponseEntity
	 * .status(HttpStatus.NOT_FOUND)
	 * .body(ApiResult.error(apiMsgHelper.getMessage(
	 * "achievementRetrieveByKeyNotFound", null), null));
	 */

	@Autowired
	private IAchievementQueryHandler achievementQueryHandler;

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
