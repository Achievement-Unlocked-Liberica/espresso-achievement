package espresso.achievement.cmd.application.commandHandlers;


import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class CreateAchivementCommand {
    private String userKey;
    private String title;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date completedDate;
    private String[] skills;
    private Boolean isPublic;

    public boolean isValid() {
        return userKey != null && !userKey.isEmpty() &&
            title != null && !title.isEmpty() &&
            description != null && !description.isEmpty() &&
            completedDate != null && !completedDate.equals(new Date(0));
    }
}