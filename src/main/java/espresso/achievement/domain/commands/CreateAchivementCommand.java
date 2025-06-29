package espresso.achievement.domain.commands;


import org.springframework.format.annotation.DateTimeFormat;

import espresso.common.domain.commands.CommonCommand;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class CreateAchivementCommand extends CommonCommand{

    @NotBlank(message = "LOCALIZE:  A USER KEY MUST BE PROVIDED")
    private String userKey;

    @NotBlank(message = "LOCALIZE: A TITLE MUST BE PROVIDED")
    private String title;

    @NotBlank(message = "LOCALIZE: A DESCRIPTION MUST BE PROVIDED")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "LOCALIZE: THE COMPLETED DATE CANNOT BE AFTER TODAY")
    private Date completedDate;

    @Size(min = 1, max = 7, message = "LOCALIZE: AT LEAST ONE SKILL MUST BE PROVIDED")
    private String[] skills;

    // @Size(min = 1, max = 10)
    // private CreateAchievementMediaCommand[] mediaFiles;
    
    private Boolean isPublic;
}