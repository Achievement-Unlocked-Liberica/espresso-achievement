package espresso.achievement.domain.commands;


import org.springframework.format.annotation.DateTimeFormat;

import espresso.common.domain.commands.CommonCommand;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class CreateAchivementCommand extends CommonCommand{

    @NotBlank(message = "LOCALIZE:  A USER KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS")
    private String userKey;

    @NotBlank(message = "LOCALIZE: A TITLE MUST BE PROVIDED")
    @Size( max = 200, message = "LOCALIZE: TITLE MUST NOT BE GREATER THAN 200 CHARACTERS")    
    private String title;

    @NotBlank(message = "LOCALIZE: A DESCRIPTION MUST BE PROVIDED")
    @Size( max = 1000, message = "LOCALIZE: TITLE MUST NOT BE GREATER THAN 1000 CHARACTERS")   
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "LOCALIZE: THE COMPLETED DATE CANNOT BE AFTER TODAY")
    private Date completedDate;    @Size(min = 1, max = 7, message = "LOCALIZE: AT LEAST ONE SKILL MUST BE PROVIDED")
    private String[] skills;
    
    private Boolean isPublic = true;

    // Allowed skill values
    private static final Set<String> ALLOWED_SKILLS = Set.of("str", "dex", "con", "wis", "int", "cha", "luc");
    private static final String ERROR_INVALID_SKILL = "LOCALIZE: INVALID SKILL '%s'. ALLOWED SKILLS ARE: str, dex, cons, wis, int, cha, luc";

    @Override
    public Set<String> validate() {
        // Call parent validation first to get standard JSR-303 validation errors
        Set<String> parentErrors = super.validate();
        
        // Create a new mutable set to avoid UnsupportedOperationException
        Set<String> errors = new HashSet<>();
        if (parentErrors != null) {
            errors.addAll(parentErrors);
        }
        
        // Validate skills if present
        if (skills != null && skills.length > 0) {
            for (int i = 0; i < skills.length; i++) {
                String skill = skills[i];
                if (skill != null && !skill.trim().isEmpty()) {
                    String normalizedSkill = skill.trim().toLowerCase();
                    if (!ALLOWED_SKILLS.contains(normalizedSkill)) {
                        errors.add("skills[" + i + "]:" + String.format(ERROR_INVALID_SKILL, skill));
                    }
                }
            }
        }
        
        return errors;
    }

    
}