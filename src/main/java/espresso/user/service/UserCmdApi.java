package espresso.user.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import espresso.common.service.CommonCmdApi;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController("User Cmd Api")
@RequestMapping("/api/cmd/user")
@Tag(name = "User Command API", description = "API for handling USer commands.")
public class UserCmdApi extends CommonCmdApi{

}
