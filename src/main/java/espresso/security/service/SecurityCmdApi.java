package espresso.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonCmdApi;
import espresso.security.domain.commands.AuthCredentialsCommand;
import espresso.security.domain.commands.RegisterUserCommand;
import espresso.security.domain.contracts.ISecurityCommandHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController("Security Cmd Api")
@RequestMapping("/api/cmd/security")
@Tag(name = "Security Command API", description = "API for handling authentication and authorization commands.")
public class SecurityCmdApi extends CommonCmdApi {

    @Autowired
    private ISecurityCommandHandler securityCommandHandler;

    @Operation(summary = "Authenticate User", description = "Authenticates user credentials and returns a JWT token.")
    @PostMapping("/auth")
    @ApiResponse(responseCode = "200:OK", description = "Authentication successful, JWT token returned.")
    @ApiResponse(responseCode = "401:UNAUTHORIZED", description = "Invalid credentials or inactive user account.")
    @ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request.")
    @ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
    public ResponseEntity<ServiceResponse<Object>> authenticate(@RequestBody AuthCredentialsCommand command) {
        return executeCommand(command, securityCommandHandler::handle);
    }

    @Operation(summary = "Register New User", description = "Registers a new user with username, email and password.")
    @PostMapping("/register")
    @ApiResponse(responseCode = "201:CREATED", description = "User registered successfully.")
    @ApiResponse(responseCode = "400:BAD_REQUEST", description = "Validation error in the request or user already exists.")
    @ApiResponse(responseCode = "500:INTERNAL_SERVER_ERROR", description = "An internal error occurred.")
    public ResponseEntity<ServiceResponse<Object>> registerUser(@RequestBody RegisterUserCommand command) {
        return executeCommand(command, securityCommandHandler::handle);
    }
}
