package espresso.achievement.cmd.domain.entities;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@NoArgsConstructor
@AllArgsConstructor
public class AchievementMedia extends Entity {
    
    @Getter
    String mediaPath;

    @Getter
    String originalName;

    @Getter
    String mimeType;

    @Getter
    Integer size;

    @Getter
    String encoding;

    //buffer: Buffer;    
}
