package espresso.achievement.cmd.service;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import espresso.achievement.cmd.application.commandHandlers.CreateAchivementCommand;
import espresso.achievement.cmd.domain.contracts.IAchievementCommandHandler;

@SpringBootTest
@AutoConfigureMockMvc
public class AchievementApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnIsHealthy() throws Exception {
        this.mockMvc
                .perform(get("/api/cmd/v1/achievement/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("API is healthy")));
    }


    @MockBean
    private IAchievementCommandHandler achivementCommandHandler;

    @Test
    void shouldReturnBadRequestForNullCommand() throws Exception {

        doNothing().when(achivementCommandHandler).handle(any(CreateAchivementCommand.class));

        this.mockMvc
                .perform(get("/api/cmd/v1/achievement")
                        .param("command", new CreateAchivementCommand().toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error creating achievement")));
    }


}
