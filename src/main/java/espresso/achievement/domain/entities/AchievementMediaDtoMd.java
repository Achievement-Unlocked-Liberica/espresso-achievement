package espresso.achievement.domain.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface AchievementMediaDtoMd {
    
    String getImageKey();
    
    String getImageName();
    
    String getOriginalImageName();
    
    String getContentType();
    
    String getMediaUrl();
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime getUploadTimestamp();
    
    Long getFileSize();
}
