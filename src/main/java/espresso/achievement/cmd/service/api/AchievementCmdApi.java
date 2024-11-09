package espresso.achievement.cmd.service.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import espresso.achievement.cmd.domain.commands.CreateAchivementCommand;
import espresso.achievement.cmd.domain.contracts.IAchievementCommandHandler;
import espresso.achievement.cmd.domain.entities.Achievement;
import espresso.achievement.common.helpers.ApiMessageHelper;
import espresso.achievement.common.response.ApiResult;
import espresso.achievement.common.response.HandlerResult;

@RestController("Achievement Cmd Api")
@RequestMapping("/api/cmd/v1/achievement")
public class AchievementCmdApi {

	@Autowired
	private ApiMessageHelper apiMsgHelper;

	@Autowired
	private IAchievementCommandHandler achivementCommandHandler;

	@Operation(summary = "Health Check", description = "Checks the health of the API.")
	@GetMapping("/health")
	@ApiResponse(responseCode = "200:OK", description = "API is healthy.")
	public ResponseEntity<ApiResult<String>> healthCheck() {

		String message = apiMsgHelper.getMessage("achievementCmdHealthy", null);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResult.success(null, message));
	}

	@Operation(summary = "Create New Achivement", description = "Creates a new Achievement from the provided command.")
	@PostMapping("")
	@ApiResponse(responseCode = "200:OK", description = "Creates a new Achievement from the provided command.")
	public ResponseEntity<ApiResult<Object>> createAchievement(@Valid @RequestBody CreateAchivementCommand command) {

		try {
			HandlerResult<Achievement> result = achivementCommandHandler.handle(command);

			return ResponseEntity
					.status(HttpStatus.OK)
					.body(ApiResult.success(
							apiMsgHelper.getMessage("achievementCreatedSuccess", null),
							result.getData()));
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResult.error(
							apiMsgHelper.getMessage("achievementCreatedFailure", null),
							e.toString()));
		}
	}
}
