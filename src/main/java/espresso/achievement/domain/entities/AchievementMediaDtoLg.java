package espresso.achievement.domain.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

// TODO: do not used, this is a palce holder for an undefined DTO we don't have requirements for
public interface AchievementMediaDtoLg {
    
    String getImageKey();
    
    String getImageName();
    
    String getOriginalImageName();
    
    String getContentType();
    
    String getMediaUrl();
    
    Long getFileSize();
}
