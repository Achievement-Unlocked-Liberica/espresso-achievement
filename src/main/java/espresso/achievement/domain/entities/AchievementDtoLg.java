package espresso.achievement.domain.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import espresso.user.domain.entities.UserDtoLg;

// TODO: do not used, this is a palce holder for an undefined DTO we don't have requirements for
public interface AchievementDtoLg {
    
    String getEntityKey();
    
    String getTitle();
    
    String getDescription();
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date getCompletedDate();
    
    UserDtoLg getUser();
    
    List<String> getSkills();
    
    AchievementVisibilityStatus getAchievementVisibility();
    
    List<AchievementMediaDtoLg> getMedia();
}
