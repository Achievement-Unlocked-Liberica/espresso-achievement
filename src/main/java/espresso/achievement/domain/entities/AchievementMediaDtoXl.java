package espresso.achievement.domain.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public interface AchievementMediaDtoXl {
    
    String getImageKey();
    
    String getImageName();
    
    String getOriginalImageName();
    
    String getContentType();
    
    String getMediaUrl();
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime getUploadTimestamp();
    
    Long getFileSize();
    
    // For XL size, we include full achievement details
    Achievement getAchievement();
    
    // Additional metadata for XL
    byte[] getImageData();
}
