package espresso.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonCmdApi;
import espresso.user.domain.commands.AddUserCommand;
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
    public ResponseEntity<ServiceResponse<Object>> createUser(@RequestBody AddUserCommand command) {

        ServiceResponse<Object> apiResponse = null;

        try {
            HandlerResponse<Object> handlerResponse = userCommandHandler.handle(command);

            apiResponse = processHandlerResult(handlerResponse);

            return ResponseEntity
                    .status(apiResponse.getHttpStatus())
                    .body(apiResponse);

        } catch (Exception ex) {
            apiResponse = processHandlerError(ex);
        }

        return ResponseEntity
                .status(apiResponse.getHttpStatus())
                .body(apiResponse);
    }
}
