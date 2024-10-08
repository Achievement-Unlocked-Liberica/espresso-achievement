package espresso.achievement.cmd.service.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController("Hello Api")
@RequestMapping("/api/cmd/v1/hello")
public class HelloApi {

    @Operation(summary = "Get Hello", description = "Says hello to the given user's first and last name.")
    @GetMapping("/greeting")
    @ApiResponse(responseCode = "200:OK", description = "Returns a greeting message.")
    public ResponseEntity<ApiResult<String>> getGreeting(@RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName) {

        return ResponseEntity.ok(ApiResult.success("Hello, " + firstName + " " + lastName + "!", null));
    }

}
