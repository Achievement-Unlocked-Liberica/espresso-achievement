package espresso.achievement.domain.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import espresso.user.domain.entities.UserDtoSm;

// TODO: do not used, this is a palce holder for an undefined DTO we don't have requirements for
public interface AchievementDtoMd {
    
    String getEntityKey();
    
    String getTitle();
    
    String getDescription();
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date getCompletedDate();
    
    UserDtoSm getUser();
    
    List<String> getSkills();
    
    AchievementVisibilityStatus getAchievementVisibility();
}
