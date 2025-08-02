package espresso.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonCmdApi;
import espresso.common.service.operational.ApiLogger;
import espresso.user.domain.commands.AddUserCommand;
import espresso.user.domain.commands.UpdateProfilePictureCommand;
import espresso.user.domain.contracts.IUserCommandHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController("User Cmd Api")
@RequestMapping("/api/cmd/user")
@Tag(name = "User Command API", description = "API for handling User commands.")
public class UserCmdApi extends CommonCmdApi {

    @Autowired
    private IUserCommandHandler userCommandHandler;

    @Operation(summary = "Create New User", description = "Creates a new User from the provided command.")
    @PostMapping("")
    @ApiResponse(responseCode = "201:CREATED", description = "Created a new User successfully.")
    @ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
    @ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
    @ApiLogger("Create new user")
    public ResponseEntity<ServiceResponse<Object>> createUser(@RequestBody AddUserCommand command) {
        return executeCommand(command, userCommandHandler::handle);
    }

    @Operation(summary = "Upload Profile Picture", description = "Upload a profile picture for the registered user.")
    @ApiResponse(responseCode = "200:OK", description = "Profile picture uploaded successfully.")
    @ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
    @ApiResponse(responseCode = "404:NOT_FOUND", description = "User not found.")
    @ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
    @PutMapping("/{key}/picture")
    @ApiLogger("Upload user profile picture")
    public ResponseEntity<ServiceResponse<Object>> updateProfilePicture(@RequestParam MultipartFile image,
            @PathVariable String key) {

        return executeCommand(new UpdateProfilePictureCommand(key, image),userCommandHandler::handle);
    }
}
