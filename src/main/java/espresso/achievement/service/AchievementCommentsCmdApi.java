package espresso.achievement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.achievement.domain.commands.AddAchievementCommentCommand;
import espresso.achievement.domain.contracts.IAchievementCommandHandler;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonCmdApi;
import espresso.common.service.operational.ApiLogger;
import espresso.security.domain.entities.JWTAuthenticationToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST API controller for handling achievement comment commands.
 * Provides endpoints for creating and managing achievement comments.
 */
@RestController("Achievement Comments Cmd Api")
@RequestMapping("/api/cmd/achievement")
@Tag(name = "Achievement Comments Command API", description = "API for handling achievement comment commands.")
public class AchievementCommentsCmdApi extends CommonCmdApi {

    @Autowired
    private IAchievementCommandHandler achievementCommandHandler;

    /**
     * Creates a new comment on an existing achievement.
     * The userKey is automatically extracted from the JWT authentication token.
     * 
     * @param command The command containing achievement key and comment text
     * @return ResponseEntity with the created comment or error response
     */
    @Operation(summary = "Add Comment to Achievement", description = "Creates a new comment on an existing achievement.")
    @PostMapping("/comments")
    @ApiResponse(responseCode = "201:CREATED", description = "Comment created successfully.")
    @ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
    @ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Unauthorized access - invalid or missing JWT token.")
    @ApiResponse(responseCode = "404:NOT_FOUND", description = "Achievement or user not found.")
    @ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
    @ApiLogger("Add achievement comment")
    public ResponseEntity<ServiceResponse<Object>> addComment(@RequestBody AddAchievementCommentCommand command) {
        
        // Get authentication from SecurityContext to extract user key from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKey = null;
        
        if (authentication instanceof JWTAuthenticationToken) {
            JWTAuthenticationToken jwtAuth = (JWTAuthenticationToken) authentication;
            userKey = jwtAuth.getUserKey();
        }

        // Set the user key from the authenticated JWT token
        command.setUserKey(userKey);
        
        return executeCommand(command, achievementCommandHandler::handleAddComment);
    }
}
