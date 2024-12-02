package espresso.achievement.domain.readModels;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AchievementSummaryReadModel {
    private final String key;
    private final String title;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date completedDate;
    private final SkillSummaryReadModel[] skills;
    private final AchievementMediaDetailReadModel[] media;
    private final UserProfileSummaryReadModel userProfile;
    // private AchievementVisibilityStatus achievementVisibility;
}
