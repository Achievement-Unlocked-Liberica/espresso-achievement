package espresso.achievement.cmd.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import espresso.achievement.cmd.application.commandHandlers.AchivementCommandHandler;
import espresso.achievement.cmd.application.commandHandlers.CreateAchivementCommand;
import espresso.achievement.cmd.application.commandHandlers.CreateAchivementCommandHandler;
import espresso.achievement.cmd.domain.contracts.IAchievementCommandHandler;

@RestController("Achievement Api")
@RequestMapping("/api/cmd/v1/achievement")
public class AchievementApi {


    @Autowired
    private IAchievementCommandHandler achivementCommandHandler;

    @Operation(summary = "Health Check", description = "Checks the health of the API.")
    @GetMapping("/health")
    @ApiResponse(responseCode = "200:OK", description = "API is healthy.")
    public ResponseEntity<ApiResult<String>> healthCheck() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.success(null, "API is healthy"));
    }

    @Operation(summary = "Create New Achivement", description = "Creates a new Achievement from the provided command.")
    @PostMapping("")
    @ApiResponse(responseCode = "200:OK", description = "Creates a new Achievement from the provided command.")
    public ResponseEntity<ApiResult<String>> createAchievement(@ModelAttribute CreateAchivementCommand command) {

        try {

            if (command != null && command.isValid()) {

                achivementCommandHandler.handle(command);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(ApiResult.success(null, "The achievement was created successfully"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResult.error("Error creating achievement \n\r" + "The command is not valid"));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResult.error("Error creating achievement \n\r" + e.toString()));
        }

    }

}
