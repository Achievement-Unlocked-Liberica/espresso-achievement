package espresso.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.CommonQryApi;
import espresso.user.domain.queries.GetUserByKeyQuery;
import espresso.user.domain.contracts.IUserQueryHandler;
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
    public ResponseEntity<ServiceResponse<Object>> getUserByKey(GetUserByKeyQuery qry) {
        ServiceResponse<Object> apiResponse = null;
        try {
            HandlerResponse<Object> handlerResponse = usersQueryHandler.handle(qry);

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
