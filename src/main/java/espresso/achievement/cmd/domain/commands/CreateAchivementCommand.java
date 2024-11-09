package espresso.achievement.cmd.domain.commands;


import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class CreateAchivementCommand {
    @NotBlank
    private String userKey;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent
    private Date completedDate;

    @Size(min = 1, max = 7)
    private String[] skills;

    @Size(min = 1, max = 10)
    private CreateMediaCommand[] mediaFiles;
    
    private Boolean isPublic;

    @Getter
    public class CreateMediaCommand {
        @NotBlank
        private String originalName;

        @NotBlank
        private String mimeType;

        @NotBlank
        private String encoding;
        
        @NotNull
        private Integer size;
    }
}