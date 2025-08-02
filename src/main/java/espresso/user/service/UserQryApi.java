package espresso.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonQryApi;
import espresso.common.service.operational.ApiLogger;
import espresso.user.domain.queries.GetUserByKeyQuery;
import espresso.user.domain.queries.GetUserNameExistsQuery;
import espresso.user.domain.queries.GetEmailExistsQuery;
import espresso.user.domain.queries.GetMyUserQuery;
import espresso.user.domain.contracts.IUserQueryHandler;
import espresso.security.domain.entities.JWTAuthenticationToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController("Users Qry Api")
@RequestMapping("/api/qry/user")
@Tag(name = "Users Query API", description = "Query API for Users")
public class UserQryApi extends CommonQryApi {

    @Autowired
    private IUserQueryHandler usersQueryHandler;

    @Operation(summary = "Get Single User By Key", description = "Get a single user by their entity key.")
    @GetMapping(value = "", headers = "X-API-Version=1")
    @ApiResponse(responseCode = "200:OK", description = "Get User by Key.")
    @ApiLogger("Get user by entity key")
    public ResponseEntity<ServiceResponse<Object>> getUserByKey(GetUserByKeyQuery qry) {
        return executeQuery(qry, usersQueryHandler::handle);
    }

    @Operation(summary = "Check if Username Exists", description = "Check if a username already exists in the system.")
    @GetMapping(value = "/check-username", headers = "X-API-Version=1")
    @ApiResponse(responseCode = "200:OK", description = "Returns true if username exists, false otherwise.")
    @ApiLogger("Check username availability")
    public ResponseEntity<ServiceResponse<Object>> checkUserNameExists(GetUserNameExistsQuery qry) {
        return executeQuery(qry, usersQueryHandler::handle);
    }

    @Operation(summary = "Check if Email Exists", description = "Check if an email already exists in the system.")
    @GetMapping(value = "/check-email", headers = "X-API-Version=1")
    @ApiResponse(responseCode = "200:OK", description = "Returns true if email exists, false otherwise.")
    @ApiLogger("Check email availability")
    public ResponseEntity<ServiceResponse<Object>> checkEmailExists(GetEmailExistsQuery qry) {
        return executeQuery(qry, usersQueryHandler::handle);
    }

    @Operation(summary = "Get My User Profile", description = "Get the authenticated user's profile information.")
    @GetMapping(value = "/me")
    @ApiResponse(responseCode = "200:OK", description = "Returns the authenticated user's profile.")
    @ApiResponse(responseCode = "401:UNAUTHORIZED", description = "User not authenticated.")
    @ApiResponse(responseCode = "404:NOT_FOUND", description = "User profile not found.")
    @ApiLogger("Get authenticated user profile")
    // public ResponseEntity<ServiceResponse<Object>> getMyUserProfile(@AuthenticationPrincipal JWTAuthenticationToken jwt) {
    public ResponseEntity<ServiceResponse<Object>> getMyUserProfile() {
        // Get authentication from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JWTAuthenticationToken) {
            JWTAuthenticationToken jwtAuth = (JWTAuthenticationToken) authentication;

            // Create query with the user's entity key
            GetMyUserQuery query = new GetMyUserQuery(jwtAuth.getUserKey());

            // Execute the query using the handler
            return executeQuery(query, usersQueryHandler::handle);
        } else {
            // If authentication is not JWT type, return unauthorized
            return ResponseEntity.status(401).body(
                    ServiceResponse.error(org.springframework.http.HttpStatus.UNAUTHORIZED,
                            "Invalid authentication type"));
        }
    }
}
