package espresso.achievement.domain.readModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MediaStorageDetailReadModel {
    
    private final String storageUrl;
    private final String containerName;
    private final String mediaPath;
    private final String accessToken;
}
