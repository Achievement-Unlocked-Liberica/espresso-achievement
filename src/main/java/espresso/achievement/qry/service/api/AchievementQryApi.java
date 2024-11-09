package espresso.achievement.qry.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.achievement.cmd.domain.commands.CreateAchivementCommand;
import espresso.achievement.cmd.domain.entities.Achievement;
import espresso.achievement.common.helpers.ApiMessageHelper;
import espresso.achievement.common.response.ApiResult;
import espresso.achievement.common.response.HandlerResult;
import espresso.achievement.qry.domain.contracts.IAchievementQueryHandler;
import espresso.achievement.qry.domain.queries.GetAchievementDetailByKeyQuery;
import espresso.achievement.qry.domain.queries.GetAchievementSummaryByKeyQuery;
import espresso.achievement.qry.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.qry.domain.readModels.AchievementSummaryReadModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController("Achievement Qry Api")
@RequestMapping("/api/qry/v1/achievement")
public class AchievementQryApi {

	@Autowired
	private ApiMessageHelper apiMsgHelper;

	@Autowired
	private IAchievementQueryHandler achievementQueryHandler;

	@Operation(summary = "Health Check", description = "Checks the health of the API.")
	@GetMapping("/health")
	@ApiResponse(responseCode = "200:OK", description = "API is healthy.")
	public ResponseEntity<ApiResult<String>> healthCheck() {

		String message = apiMsgHelper.getMessage("achievementQryHealthy", null);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResult.success(null, message));
	}

	@Operation(summary = "Get Achievement by Key", description = "Retrieves an existing Achievemnt by its Key.")
	@GetMapping("/detail")
	@ApiResponse(responseCode = "200:OK", description = "Retrieves an existing Achievemnt by its Key.")
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
}
