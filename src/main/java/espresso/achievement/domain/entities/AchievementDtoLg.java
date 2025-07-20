package espresso.achievement.domain.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import espresso.user.domain.entities.UserDtoMd;

public interface AchievementDtoLg {
    
    String getEntityKey();
    
    String getTitle();
    
    String getDescription();
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Date getCompletedDate();
    
    UserDtoMd getUser();
    
    List<String> getSkills();
    
    AchievementVisibilityStatus getAchievementVisibility();
    
    List<AchievementMediaDtoMd> getMedia();
}
