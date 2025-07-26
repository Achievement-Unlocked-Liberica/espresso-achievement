package espresso.achievement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.ApiMessageHelper;
import espresso.achievement.application.response.HandlerResult;
import espresso.achievement.domain.contracts.IAchievementQueryHandler;
import espresso.achievement.domain.queries.GetAchievementDetailByKeyQuery;
import espresso.achievement.domain.queries.GetAchievementSummariesByUserQuery;
import espresso.achievement.domain.queries.GetAchievementSummaryByKeyQuery;
import espresso.achievement.domain.queries.GetLatestAchievementsQuery;
import espresso.achievement.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.domain.readModels.AchievementSummaryReadModel;
import espresso.achievement.domain.readModels.MediaStorageDetailReadModel;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonQryApi;
import espresso.common.service.operational.ApiLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController("Achievement Qry Api")
@RequestMapping("/api/qry/v1/achievement")
public class AchievementQryApi extends CommonQryApi {

	@Autowired
	private ApiMessageHelper apiMsgHelper;

	@Autowired
	private IAchievementQueryHandler achievementQueryHandler;


	@Operation(summary = "Get Achievement by Key", description = "Retrieves an existing Achievemnt by its Key.")
	@GetMapping("/detail")
	@ApiResponse(responseCode = "200:OK", description = "Retrieves an existing Achievemnt by its Key.")
	@ApiLogger("Get achievement detail by key")
	public ResponseEntity<ApiResult<Object>> getAchivementDetailByKey(
		@Valid GetAchievementDetailByKeyQuery query) {

		try {
			HandlerResult<AchievementDetailReadModel> result = achievementQueryHandler.handle(query);

			if (result.HasData()) {
				return ResponseEntity
						.status(HttpStatus.OK)
						.body(ApiResult.success(null, result.getData()));
			} else {
				return ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body(ApiResult.error(apiMsgHelper.getMessage("achievementRetrieveByKeyNotFound", null), null));
			}

		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResult.error(apiMsgHelper.getMessage("achievementRetrieveByKeyFailure", null),
							e.toString()));
		}
	}

	@Operation(summary = "Get Achievement by Key", description = "Retrieves an existing Achievemnt by its Key.")
	@GetMapping("/summary")
	@ApiResponse(responseCode = "200:OK", description = "Retrieves an existing Achievemnt by its Key.")
	@ApiLogger("Get achievement summary by key")
	public ResponseEntity<ApiResult<Object>> getAchivementSummaryByKey(
		@Valid GetAchievementSummaryByKeyQuery query) {

		try {
			HandlerResult<AchievementSummaryReadModel> result = achievementQueryHandler.handle(query);

			if (result.HasData()) {
				return ResponseEntity
						.status(HttpStatus.OK)
						.body(ApiResult.success(null, result.getData()));
			} else {
				return ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body(ApiResult.error(apiMsgHelper.getMessage("achievementRetrieveByKeyNotFound", null), null));
			}

		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResult.error(apiMsgHelper.getMessage("achievementRetrieveByKeyFailure", null),
							e.toString()));
		}
	}

	@Operation(summary = "Get Achievements by User", description = "Retrieves the existing Achievements for a user.")
	@GetMapping("/summary/user")
	@ApiResponse(responseCode = "200:OK", description = "Retrieves the existing Achievements for a user.")
	@ApiLogger("Get achievement summaries by user")
	public ResponseEntity<ApiResult<Object>> getAchivementSummariesByUser(
		@Valid GetAchievementSummariesByUserQuery query) {

		try {
			HandlerResult<List<AchievementSummaryReadModel>> result = achievementQueryHandler.handle(query);

			if (result.HasData()) {
				return ResponseEntity
						.status(HttpStatus.OK)
						.body(ApiResult.success(null, result.getData()));
			} else {
				return ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body(ApiResult.error(apiMsgHelper.getMessage("achievementRetrieveByKeyNotFound", null), null));
			}

		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResult.error(apiMsgHelper.getMessage("achievementRetrieveByKeyFailure", null),
							e.toString()));
		}
	}



	@Operation(summary = "Get Latest Achievements", description = "Retrieves the latest achievements ordered by completion date (newest first).")
	@GetMapping("/latest")
	@ApiResponse(responseCode = "200:OK", description = "Returns the latest achievements in the specified DTO format.")
	@ApiLogger("Get latest achievements")
	public ResponseEntity<ServiceResponse<Object>> getLatestAchievements(GetLatestAchievementsQuery qry) {

		return executeQuery(qry, achievementQueryHandler::handle);
	}


}
