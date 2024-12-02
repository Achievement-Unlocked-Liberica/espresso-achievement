package espresso.achievement.domain.commands;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class CreateAchievementMediaCommand {
    @NotBlank
    private String originalName;

    @NotBlank
    private String mimeType;

    @NotBlank
    private String encoding;
    
    @NotNull
    private Integer size;
}
