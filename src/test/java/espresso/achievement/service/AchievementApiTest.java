package espresso.achievement.service;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;

import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.contracts.IAchievementCommandHandler;

@SpringBootTest
@AutoConfigureMockMvc
public class AchievementApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    @Test
    void shouldReturnIsHealthy() throws Exception {

        String message = messageSource.getMessage("achievementHealthy", null, Locale.getDefault());

        this.mockMvc
                .perform(get("/api/cmd/v1/achievement/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(message)));
    }

    @MockBean
    private IAchievementCommandHandler achivementCommandHandler;

    @Test
    void shouldReturnBadRequestForEmptyCommandBody() throws Exception {

        doNothing().when(achivementCommandHandler).handle(any(CreateAchivementCommand.class));

        String message = messageSource.getMessage("commandValidationFailure", null, Locale.getDefault());

        this.mockMvc
                .perform(post("/api/cmd/v1/achievement")
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(message)));
    }

    @Test
    void shouldReturnBadRequestForEmptyCommandProperties() throws Exception {

        doNothing().when(achivementCommandHandler).handle(any(CreateAchivementCommand.class));

        String message = messageSource.getMessage("commandValidationFailure", null, Locale.getDefault());

        this.mockMvc
                .perform(post("/api/cmd/v1/achievement")
                        .content(new CreateAchivementCommand().toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(message)));
    }

    /**
     * Test case: shouldReturnBadRequestForIncompleteCommandProperties
     * 
     * This test verifies that the Achievement API returns a Bad Request (400) status
     * when an incomplete CreateAchievementCommand is submitted. Specifically, it 
     * checks that the API correctly handles validation failures by:
     * 
     * 2. Asserting that the response content contains the expected validation failure 
     *    message.
     * 
     * This ensures that the API enforces proper validation rules and provides meaningful 
     * feedback to the client when required command properties are missing or invalid.
     */
    @Test
    void shouldReturnBadRequestForIncompleteCommandProperties() throws Exception {

        doNothing().when(achivementCommandHandler).handle(any(CreateAchivementCommand.class));

        String message = messageSource.getMessage("commandValidationFailure", null, Locale.getDefault());

        CreateAchivementCommand command = new CreateAchivementCommand();
        command.setUserKey("1234567");
        command.setTitle("");
        command.setDescription("Description for a new achievement");

        LocalDate localDate = LocalDate.now().minusDays(2);
        Date completedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        command.setCompletedDate(completedDate);
        command.setSkills(new String[] {});

        this.mockMvc
                .perform(post("/api/cmd/v1/achievement")
                        .content(new CreateAchivementCommand().toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(message)));
    }

}
