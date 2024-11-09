package espresso.achievement.cmd.domain.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class AchivementTests {

    private UserProfile userProfile;
    private List<Skill> skills;
    private List<AchievementMedia> media;

    @BeforeEach
    public void setUp() {
        userProfile = new UserProfile();
        userProfile.setKey(UUID.randomUUID().toString());

        Skill skill = new Skill();
        skill.setKey(UUID.randomUUID().toString());
        skills = Arrays.asList(skill);

        AchievementMedia achievementMedia = new AchievementMedia();
        achievementMedia.setKey(UUID.randomUUID().toString());
        media = Arrays.asList(achievementMedia);
    }

    /**
     * Tests the creation of a public achievement.
     * 
     * This test verifies that an achievement can be created with the specified title, 
     * description, completion date, and visibility status set to public. It also checks 
     * that the created achievement has the correct user profile, skills, and media associated with it.
     * 
     * The following assertions are made:
     * - The created achievement is not null.
     * - The title of the achievement matches the expected title.
     * - The description of the achievement matches the expected description.
     * - The completion date of the achievement matches the expected completion date.
     * - The visibility status of the achievement is set to public (EVERYONE).
     * - The user profile associated with the achievement matches the expected user profile.
     * - The skills associated with the achievement match the expected skills.
     * - The media associated with the achievement match the expected media.
     */
    @Test
    public void testCreateAchievement_Public() {
        String title = "Achievement Title";
        String description = "Achievement Description";
        Date completedDate = new Date();
        boolean isPublic = true;

        Achievement achievement = Achievement.create(title, description, completedDate, isPublic, userProfile, skills, media);

        assertNotNull(achievement);
        assertEquals(title, achievement.getTitle());
        assertEquals(description, achievement.getDescription());
        assertEquals(completedDate, achievement.getCompletedDate());
        assertEquals(Achievement.AchievementVisibilityStatus.EVERYONE, achievement.getAchievementVisibility());
        assertEquals(userProfile, achievement.getUserProfile());
        assertEquals(skills, achievement.getSkills());
        assertEquals(media, achievement.getMedia());
    }

    /**
     * Tests the creation of a private achievement.
     * 
     * This test verifies that an achievement is correctly created with the specified
     * title, description, completion date, and visibility status set to private.
     * It also checks that the achievement is associated with the correct user profile,
     * skills, and media.
     * 
     * The following assertions are made:
     * - The created achievement is not null.
     * - The title of the achievement matches the expected title.
     * - The description of the achievement matches the expected description.
     * - The completion date of the achievement matches the expected completion date.
     * - The visibility status of the achievement is set to private.
     * - The user profile associated with the achievement matches the expected user profile.
     * - The skills associated with the achievement match the expected skills.
     * - The media associated with the achievement match the expected media.
     */
    @Test
    public void testCreateAchievement_Private() {
        String title = "Achievement Title";
        String description = "Achievement Description";
        Date completedDate = new Date();
        boolean isPublic = false;

        Achievement achievement = Achievement.create(title, description, completedDate, isPublic, userProfile, skills, media);

        assertNotNull(achievement);
        assertEquals(title, achievement.getTitle());
        assertEquals(description, achievement.getDescription());
        assertEquals(completedDate, achievement.getCompletedDate());
        assertEquals(Achievement.AchievementVisibilityStatus.PRIVATE, achievement.getAchievementVisibility());
        assertEquals(userProfile, achievement.getUserProfile());
        assertEquals(skills, achievement.getSkills());
        assertEquals(media, achievement.getMedia());
    }


    @Test
    public void testSetSkills() {

        Achievement achievement = new Achievement(true);
        Skill skill1 = new Skill("Skill1", "sk1", "");
        Skill skill2 = new Skill("Skill2", "sk2", "");
        List<Skill> skills = Arrays.asList(skill1, skill2);

        achievement.setSkills(skills);

        assertEquals(skills, achievement.getSkills());
    }

}
