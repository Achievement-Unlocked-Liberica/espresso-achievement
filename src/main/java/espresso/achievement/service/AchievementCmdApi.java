package espresso.achievement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.commands.UploadAchievementMediaCommand;
import espresso.achievement.domain.contracts.IAchievementCommandHandler;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonCmdApi;
import espresso.common.service.operational.ApiLogger;

@RestController("Achievement Cmd Api")
@RequestMapping("/api/cmd/achievement")
@Tag(name = "Achievement Command API", description = "API for handling Achievement commands.")
public class AchievementCmdApi extends CommonCmdApi {

	@Autowired
	private IAchievementCommandHandler achivementCommandHandler;

	@Operation(summary = "Create New Achivement", description = "Creates a new Achievement from the provided command.")
	@PostMapping("")
	@ApiResponse(responseCode = "201:CREATED", description = "Created a new Achievement successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Create new achievement")
	public ResponseEntity<ServiceResponse<Object>> createAchievement(@RequestBody CreateAchivementCommand command) {
		return executeCommand(command, achivementCommandHandler::handle);
	}

	@Operation(summary = "Upload Achievement Media", description = "Uploads media file for an existing Achievement.")
	@PostMapping("/{key}/media")
	@ApiResponse(responseCode = "201:CREATED", description = "Media uploaded successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Achievement not found.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Upload achievement media")
	public ResponseEntity<ServiceResponse<Object>> uploadAchievementMedia(			
			@RequestParam("image") MultipartFile image,
			@PathVariable String key) {

		UploadAchievementMediaCommand command = new UploadAchievementMediaCommand(key, image);
		return executeCommand(command, achivementCommandHandler::handleUploadMedia);
	}
}
