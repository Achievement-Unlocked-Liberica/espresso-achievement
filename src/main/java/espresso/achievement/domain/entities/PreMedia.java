package espresso.achievement.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PreMedia {

    private String storageUrl;
    private String containerName;
    private String mediaPath;
    private String accessToken;

}
