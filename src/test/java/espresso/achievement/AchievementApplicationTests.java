package espresso.achievement;

import static org.assertj.core.api.Assertions.assertThat; // Add this import statement

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import espresso.achievement.cmd.service.api.AchievementApi;

@SpringBootTest
class AchievementApplicationTests {

	@Autowired
	private AchievementApi achievementApi;


	@Test
	void contextLoads() throws Exception {
		assertThat(achievementApi).isNotNull(); // Modify the assertion statement
	}

}
