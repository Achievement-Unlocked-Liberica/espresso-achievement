package espresso.achievement.domain.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import espresso.user.domain.entities.UserDtoLg;

public interface AchievementDtoXl {
    
    String getEntityKey();
    
    String getTitle();
    
    String getDescription();
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date getCompletedDate();
    
    UserDtoLg getUser();
    
    List<String> getSkills();
    
    AchievementVisibilityStatus getAchievementVisibility();
    
    List<AchievementMediaDtoLg> getMedia();
}
