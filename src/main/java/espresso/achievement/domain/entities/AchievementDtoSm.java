package espresso.achievement.domain.entities;

import java.util.List;

import espresso.user.domain.entities.UserDtoSm;

public interface AchievementDtoSm {
    
    String getEntityKey();
    
    String getTitle();
    
    String getDescription();

    List<String> getSkills();

    UserDtoSm getUser();

    List<AchievementMediaDtoSm> getMedia();
}
