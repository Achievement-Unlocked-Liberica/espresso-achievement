package espresso.achievement.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import espresso.achievement.domain.commands.AddAchievementCelebrationCommand;
import espresso.achievement.domain.commands.AddAchievementCommentCommand;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.commands.DeleteAchievementCommand;
import espresso.achievement.domain.commands.DisableAchievementCommand;
import espresso.achievement.domain.commands.UpdateAchievementCommand;
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
	private IAchievementCommandHandler achievementCommandHandler;

	@Operation(summary = "Create New Achivement", description = "Creates a new Achievement from the provided command.")
	@PostMapping("")
	@ApiResponse(responseCode = "201:CREATED", description = "Created a new Achievement successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Create new achievement")
	public ResponseEntity<ServiceResponse<Object>> createAchievement(@RequestBody CreateAchivementCommand command) {

		String userKey = getAuthenticatedUserKey();

		command.setUserKey(userKey);

		return executeCommand(command, achievementCommandHandler::handle);
	}

	@Operation(summary = "Upload Achievement Media", description = "Uploads media files for an existing Achievement.")
	@PostMapping("/{key}/media")
	@ApiResponse(responseCode = "201:CREATED", description = "Media uploaded successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Achievement not found.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "User not authorized to upload media for this achievement.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Upload achievement media")
	public ResponseEntity<ServiceResponse<Object>> uploadAchievementMedia(
			@RequestParam("images") MultipartFile[] images,
			@PathVariable String key) {

		String userKey = getAuthenticatedUserKey();

		UploadAchievementMediaCommand command = new UploadAchievementMediaCommand(key, userKey, images);

		return executeCommand(command, achievementCommandHandler::handle);
	}

	/**
	 * Creates a new comment on an existing achievement.
	 * The userKey is automatically extracted from the JWT authentication token.
	 * 
	 * @param command The command containing achievement key and comment text
	 * @return ResponseEntity with the created comment or error response
	 */
	@Operation(summary = "Add Comment to Achievement", description = "Creates a new comment on an existing achievement.")
	@PostMapping("/{key}/comments")
	@ApiResponse(responseCode = "201:CREATED", description = "Comment created successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access - invalid or missing JWT token.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Achievement or user not found.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Add achievement comment")
	public ResponseEntity<ServiceResponse<Object>> addComment(@PathVariable String key, @RequestBody AddAchievementCommentCommand command) {
		String userKey = getAuthenticatedUserKey();
		command.setUserKey(userKey);
		command.setAchievementKey(key);
		return executeCommand(command, achievementCommandHandler::handle);
	}

	@Operation(summary = "Add Achievement Celebration", description = "Adds a celebration to an existing achievement from the authenticated user.")
	@PostMapping("/{key}/celebration")
	@ApiResponse(responseCode = "201:CREATED", description = "Celebration added successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access - invalid or missing JWT token.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Achievement or user not found.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Add achievement celebration")
	public ResponseEntity<ServiceResponse<Object>> addAchievementCelebration(@PathVariable String key, @RequestBody AddAchievementCelebrationCommand command) {
		String userKey = getAuthenticatedUserKey();
		command.setUserKey(userKey);
		command.setAchievementKey(key);
		return executeCommand(command, achievementCommandHandler::handle);
	}

	/**
	 * Updates an existing achievement with new title, description, skills, and
	 * visibility.
	 * The userKey is automatically extracted from the JWT authentication token.
	 * 
	 * @param command The command containing updated achievement data
	 * @return ResponseEntity with the updated achievement or error response
	 */
	@Operation(summary = "Update Achievement", description = "Updates an existing achievement with new details.")
	@PutMapping("/{key}")
	@ApiResponse(responseCode = "200:OK", description = "Achievement updated successfully.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access - invalid or missing JWT token or user not authorized to update this achievement.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Achievement or user not found.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Update achievement")
	public ResponseEntity<ServiceResponse<Object>> updateAchievement(@PathVariable String key, @RequestBody UpdateAchievementCommand command) {
		String userKey = getAuthenticatedUserKey();
		command.setUserKey(userKey);
		command.setAchievementKey(key);
		return executeCommand(command, achievementCommandHandler::handle);
	}

	/**
	 * Disables an existing achievement by setting its enabled property to false.
	 * This removes the achievement from all filters, searches, and visibility without deleting it from the database.
	 * The userKey is automatically extracted from the JWT authentication token.
	 * 
	 * @param key The 7-character alphanumeric key of the achievement to disable
	 * @return ResponseEntity with the disabled achievement or error response
	 */
	@Operation(summary = "Disable Achievement", description = "Disables an achievement by setting its enabled property to false.")
	@PatchMapping("/{key}/disable")
	@ApiResponse(responseCode = "200:OK", description = "Achievement disabled successfully.")
	@ApiResponse(responseCode = "204:NO_CONTENT", description = "No action taken becasue the achievement was already disabled.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access - invalid or missing JWT token or user not authorized to disable this achievement.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Achievement or user not found.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
	@ApiLogger("Disable achievement")
	public ResponseEntity<ServiceResponse<Object>> disableAchievement(@PathVariable String key) {
		String userKey = getAuthenticatedUserKey();

		DisableAchievementCommand command = new DisableAchievementCommand(key, userKey);

		return executeCommand(command, achievementCommandHandler::handle);
	}

	/**
	 * Deletes an existing achievement by permanently removing it and all associated data from the database.
	 * This removes the achievement from all filters, searches, and visibility and cannot be undone.
	 * The userKey is automatically extracted from the JWT authentication token.
	 * All dependencies (comments and media) are deleted in proper order to maintain referential integrity.
	 * 
	 * @param key The 7-character alphanumeric key of the achievement to delete
	 * @return ResponseEntity with success confirmation or error response
	 */
	@Operation(summary = "Delete Achievement", description = "Permanently deletes an achievement and all associated data from the database.")
	@DeleteMapping("/{key}")
	@ApiResponse(responseCode = "200:OK", description = "Achievement deleted successfully.")
	@ApiResponse(responseCode = "204:NO_CONTENT", description = "No action taken because the achievement didn't exist and was not deleted.")
	@ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
	@ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access - invalid or missing JWT token or user not authorized to delete this achievement.")
	@ApiResponse(responseCode = "403:FORBIDDEN", description = "User lacks authorization to delete this achievement.")
	@ApiResponse(responseCode = "404:NOT_FOUND", description = "Achievement or user not found.")
	@ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred during deletion.")
	@ApiLogger("Delete achievement")
	public ResponseEntity<ServiceResponse<Object>> deleteAchievement(@PathVariable String key) {
		String userKey = getAuthenticatedUserKey();

		DeleteAchievementCommand command = new DeleteAchievementCommand(key, userKey);

		return executeCommand(command, achievementCommandHandler::handle);
	}
}
