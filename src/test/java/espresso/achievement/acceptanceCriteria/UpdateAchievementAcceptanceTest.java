package espresso.achievement.acceptanceCriteria;

import com.fasterxml.jackson.databind.ObjectMapper;
import espresso.achievement.domain.commands.UpdateAchievementCommand;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.infrastructure.repositories.AchievementMediaS3Provider;
import espresso.achievement.infrastructure.repositories.AchievementPSQLProvider;
import espresso.security.domain.entities.JWTAuthenticationToken;
import espresso.user.domain.entities.User;
import espresso.user.domain.entities.UserKto;
import espresso.user.infrastructure.repositories.UserPSQLProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Acceptance Criteria Tests for Update Achievement Endpoint
 * Based on: src/main/java/espresso/achievement/service/acceptanceCriteria/update-achievement-ac.md
 * 
 * Testing Strategy:
 * - Uses @SpringBootTest to load full application context
 * - Uses @AutoConfigureMockMvc for API layer testing with MockMvc
 * - Mocks PSQLProvider and S3Provider to avoid database/storage dependencies
 * - Tests from API layer down through domain logic
 * - Focuses on behavior coverage over code coverage
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Update Achievement - Acceptance Criteria")
class UpdateAchievementAcceptanceTest {

    // ===== Constants =====
    private static final String API_ENDPOINT = "/api/cmd/achievement";
    private static final String VALID_USER_KEY = "ABC1234";
    private static final String INVALID_USER_KEY = "XYZ9999";
    private static final String OTHER_USER_KEY = "OTHER12";
    private static final String VALID_ACHIEVEMENT_KEY = "8NctRKY";
    private static final String INVALID_ACHIEVEMENT_KEY = "INVALID";
    
    private static final String DEFAULT_TITLE = "Updated Achievement";
    private static final String DEFAULT_DESCRIPTION = "Updated description";
    private static final String[] DEFAULT_SKILLS = {"int"};
    
    // Error messages
    private static final String ERROR_USER_NOT_FOUND = "User not found";
    private static final String ERROR_ACHIEVEMENT_NOT_FOUND = "ACHIEVEMENT NOT FOUND";
    private static final String ERROR_NOT_AUTHORIZED = "NOT AUTHORIZED";
    private static final String ERROR_TITLE_REQUIRED = "TITLE MUST BE PROVIDED";
    private static final String ERROR_TITLE_MAX_LENGTH = "200 CHARACTERS";
    private static final String ERROR_INVALID_SKILL = "INVALID SKILL";
    private static final String ERROR_NO_SKILLS = "AT LEAST ONE SKILL";
    
    // Valid skills
    private static final String[] ALL_VALID_SKILLS = {"str", "dex", "con", "wis", "int", "cha", "luc"};

    // ===== Dependencies =====
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Mock bean for Achievement repository.
     * Note: @MockBean is deprecated since Spring Boot 3.4.0 and marked for removal.
     * Will be replaced with @MockitoBean (requires spring-boot-testcontainers dependency).
     * Suppressing warning until dependency is added.
     */
    @SuppressWarnings("removal")
    @MockBean
    private AchievementPSQLProvider achievementPSQLProvider;

    /**
     * Mock bean for Achievement Media S3 provider.
     * Note: @MockBean is deprecated since Spring Boot 3.4.0 and marked for removal.
     */
    @SuppressWarnings("removal")
    @MockBean
    private AchievementMediaS3Provider achievementMediaS3Provider;

    /**
     * Mock bean for User repository.
     * Note: @MockBean is deprecated since Spring Boot 3.4.0 and marked for removal.
     */
    @SuppressWarnings("removal")
    @MockBean
    private UserPSQLProvider userPSQLProvider;

    private UserKto mockUser;
    private Achievement mockAchievement;

    // ===== Setup =====
    @BeforeEach
    void setUp() {
        mockUser = createMockUser(VALID_USER_KEY);
        mockAchievement = createMockAchievement(VALID_ACHIEVEMENT_KEY, mockUser);
    }

    // ===== Helper Methods: Mock Creation =====
    
    /**
     * Creates a mock UserKto with the specified key.
     */
    private UserKto createMockUser(String userKey) {
        return new UserKto() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getEntityKey() {
                return userKey;
            }
        };
    }

    /**
     * Creates a mock Achievement with the specified entity key and user.
     */
    private Achievement createMockAchievement(String entityKey, UserKto user) {
        Achievement achievement = new Achievement();
        achievement.setEntityKey(entityKey);
        achievement.setUser(User.fromKto(user));
        achievement.setTitle("Original Title");
        achievement.setDescription("Original Description");
        return achievement;
    }

    // ===== Helper Methods: Mock Setup =====
    
    /**
     * Sets up mocks for successful update scenario.
     */
    private void setupSuccessfulUpdateMocks(String userKey, String achievementKey) {
        when(userPSQLProvider.findByKey(userKey, UserKto.class)).thenReturn(mockUser);
        when(achievementPSQLProvider.findAchievementByKey(Achievement.class, achievementKey)).thenReturn(mockAchievement);
        when(achievementPSQLProvider.save(any(Achievement.class))).thenReturn(mockAchievement);
    }

    /**
     * Sets up mocks for user not found scenario.
     */
    private void setupUserNotFoundMocks(String userKey) {
        when(userPSQLProvider.findByKey(userKey, UserKto.class)).thenReturn(null);
    }

    /**
     * Sets up mocks for achievement not found scenario.
     */
    private void setupAchievementNotFoundMocks(String userKey, String achievementKey) {
        when(userPSQLProvider.findByKey(userKey, UserKto.class)).thenReturn(mockUser);
        when(achievementPSQLProvider.findAchievementByKey(Achievement.class, achievementKey)).thenReturn(null);
    }

    /**
     * Sets up mocks for unauthorized access scenario (different owner).
     */
    private void setupUnauthorizedMocks(String userKey, String achievementKey) {
        when(userPSQLProvider.findByKey(userKey, UserKto.class)).thenReturn(mockUser);
        UserKto otherUser = createMockUser(OTHER_USER_KEY);
        Achievement otherUserAchievement = createMockAchievement(achievementKey, otherUser);
        when(achievementPSQLProvider.findAchievementByKey(Achievement.class, achievementKey)).thenReturn(otherUserAchievement);
    }

    // ===== Helper Methods: Command Builders =====
    
    /**
     * Builder for UpdateAchievementCommand with fluent API.
     */
    private static class CommandBuilder {
        private final UpdateAchievementCommand command = new UpdateAchievementCommand();

        public CommandBuilder title(String title) {
            command.setTitle(title);
            return this;
        }

        public CommandBuilder description(String description) {
            command.setDescription(description);
            return this;
        }

        public CommandBuilder skills(String... skills) {
            command.setSkills(skills);
            return this;
        }

        public CommandBuilder isPublic(Boolean isPublic) {
            command.setIsPublic(isPublic);
            return this;
        }

        public UpdateAchievementCommand build() {
            return command;
        }
    }

    /**
     * Creates a command builder with default valid values.
     */
    private CommandBuilder validCommand() {
        return new CommandBuilder()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .skills(DEFAULT_SKILLS)
            .isPublic(true);
    }

    // ===== Helper Methods: Request Execution =====
    
    /**
     * Creates JWT authentication token for testing.
     * This simulates a valid JWT authentication with the given user key.
     */
    private RequestPostProcessor withJwtAuth(String userKey) {
        JWTAuthenticationToken jwtToken = new JWTAuthenticationToken(
            "testuser",
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            userKey,
            "test@example.com"
        );
        return authentication(jwtToken);
    }

    /**
     * Performs PUT request to update achievement endpoint.
     */
    private ResultActions performUpdateAchievement(String userKey, String achievementKey, UpdateAchievementCommand command) throws Exception {
        return mockMvc.perform(put(API_ENDPOINT + "/{key}", achievementKey)
            .with(withJwtAuth(userKey))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(command)));
    }

    /**
     * Performs PUT request without authentication.
     */
    private ResultActions performUnauthenticatedRequest(String achievementKey, UpdateAchievementCommand command) throws Exception {
        return mockMvc.perform(put(API_ENDPOINT + "/{key}", achievementKey)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(command)));
    }

    // ===== Helper Methods: Assertions =====
    
    /**
     * Asserts successful update response (200 OK).
     * Validates: Content-Type, HTTP Status, Success Flag, Response Data, HTTP Status in Response
     */
    private void assertSuccessfulUpdate(ResultActions result, String expectedKey) throws Exception {
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.entityKey").value(expectedKey))
            .andExpect(jsonPath("$.httpStatus").value("OK"));
    }

    /**
     * Asserts validation error response (400 Bad Request).
     * Validates: Content-Type, HTTP Status, Success Flag, Error Message, HTTP Status in Response
     */
    private void assertValidationError(ResultActions result, String errorMessageFragment) throws Exception {
        result.andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").value(containsString(errorMessageFragment)))
            .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));
    }

    /**
     * Asserts unauthorized response (401 Unauthorized).
     * Validates: HTTP Status (401)
     */
    private void assertUnauthorized(ResultActions result) throws Exception {
        result.andExpect(status().isUnauthorized());
    }

    /**
     * Asserts not found response (404 Not Found).
     * Validates: Content-Type, HTTP Status, Success Flag, Not Found Message, HTTP Status in Response
     */
    private void assertNotFound(ResultActions result, String resourceType) throws Exception {
        result.andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").value(containsString(resourceType)))
            .andExpect(jsonPath("$.httpStatus").value("NOT_FOUND"));
    }

    /**
     * Verifies repository interactions for successful update.
     */
    private void verifySuccessfulUpdateInteractions(String userKey, String achievementKey) {
        verify(userPSQLProvider).findByKey(userKey, UserKto.class);
        verify(achievementPSQLProvider).findAchievementByKey(Achievement.class, achievementKey);
        verify(achievementPSQLProvider).save(any(Achievement.class));
    }

    /**
     * Verifies no repository interactions occurred.
     */
    private void verifyNoRepositoryInteractions() {
        verify(achievementPSQLProvider, never()).save(any());
        verify(achievementPSQLProvider, never()).findAchievementByKey(any(), any());
    }

    // ===== Test Classes =====

    @Nested
    @DisplayName("AC1: Successful Achievement Update")
    class SuccessfulUpdate {

        @Test
        @DisplayName("AC1: Should update achievement with valid data and return 200 OK")
        void updateAchievementSuccess() throws Exception {
            // Given: Mock successful update scenario
            setupSuccessfulUpdateMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
            
            UpdateAchievementCommand command = validCommand()
                .title("Updated Multi File Upload System")
                .description("Successfully implemented and enhanced multi file upload for achievement media with validation.")
                .skills("int", "wis", "con")
                .isPublic(false)
                .build();

            // When: PUT request to update achievement
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Achievement updated successfully
            assertSuccessfulUpdate(result, VALID_ACHIEVEMENT_KEY);
            verifySuccessfulUpdateInteractions(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
        }

        @Test
        @DisplayName("AC1: Should accept all seven valid skills (str, dex, con, wis, int, cha, luc)")
        void updateAchievementWithValidSkills() throws Exception {
            // Given: Mock successful update with all valid skills
            setupSuccessfulUpdateMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
            
            UpdateAchievementCommand command = validCommand()
                .title("Skills Test Achievement")
                .description("Testing valid skills in update")
                .skills(ALL_VALID_SKILLS)
                .build();

            // When: PUT request with all valid skills
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Achievement updated successfully
            assertSuccessfulUpdate(result, VALID_ACHIEVEMENT_KEY);
            verify(achievementPSQLProvider).save(any(Achievement.class));
        }
    }

    @Nested
    @DisplayName("AC2: Authentication Failures")
    class AuthenticationFailures {

        @Test
        @DisplayName("AC2.1: Should return 401 Unauthorized when JWT token is missing")
        void missingJwtToken() throws Exception {
            // Given: Valid command but no authentication
            UpdateAchievementCommand command = validCommand().build();

            // When: PUT request without JWT token
            ResultActions result = performUnauthenticatedRequest(VALID_ACHIEVEMENT_KEY, command);

            // Then: Request rejected with 401 Unauthorized
            assertUnauthorized(result);
            verifyNoRepositoryInteractions();
        }

        @Test
        @DisplayName("AC2.2: Should return 401 Unauthorized when JWT token is invalid")
        void invalidJwtToken() throws Exception {
            // Given: Valid command but invalid token
            UpdateAchievementCommand command = validCommand().build();

            // When: PUT request with invalid token
            ResultActions result = mockMvc.perform(put(API_ENDPOINT + "/{key}", VALID_ACHIEVEMENT_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer invalid-token")
                .content(objectMapper.writeValueAsString(command)));

            // Then: Unauthorized response
            assertUnauthorized(result);
            verifyNoRepositoryInteractions();
        }
    }

    @Nested
    @DisplayName("AC3: User Not Found")
    class UserNotFound {

        @Test
        @DisplayName("AC3: Should return 404 Not Found when user doesn't exist")
        void userNotFound() throws Exception {
            // Given: User doesn't exist in system
            setupUserNotFoundMocks(INVALID_USER_KEY);
            
            UpdateAchievementCommand command = validCommand().build();

            // When: PUT request with non-existent user
            ResultActions result = performUpdateAchievement(INVALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Returns 404 Not Found with user not found message
            assertNotFound(result, ERROR_USER_NOT_FOUND);

            // Verify no update occurred
            verify(userPSQLProvider).findByKey(INVALID_USER_KEY, UserKto.class);
            verify(achievementPSQLProvider, never()).save(any());
        }
    }

    @Nested
    @DisplayName("AC4: Achievement Not Found")
    class AchievementNotFound {

        @Test
        @DisplayName("AC4: Should return 404 Not Found when achievement doesn't exist")
        void achievementNotFound() throws Exception {
            // Given: Valid user but achievement doesn't exist
            setupAchievementNotFoundMocks(VALID_USER_KEY, INVALID_ACHIEVEMENT_KEY);
            
            UpdateAchievementCommand command = validCommand().build();

            // When: PUT request for non-existent achievement
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, INVALID_ACHIEVEMENT_KEY, command);

            // Then: Returns 404 Not Found with achievement not found message
            assertNotFound(result, ERROR_ACHIEVEMENT_NOT_FOUND);

            // Verify no update occurred
            verify(userPSQLProvider).findByKey(VALID_USER_KEY, UserKto.class);
            verify(achievementPSQLProvider).findAchievementByKey(Achievement.class, INVALID_ACHIEVEMENT_KEY);
            verify(achievementPSQLProvider, never()).save(any());
        }
    }

    @Nested
    @DisplayName("AC5: Unauthorized Access (Not Owner)")
    class UnauthorizedAccess {

        @Test
        @DisplayName("AC5: Should return 401 Unauthorized when user doesn't own achievement")
        void notOwner() throws Exception {
            // Given: Valid user trying to update another user's achievement
            setupUnauthorizedMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
            
            UpdateAchievementCommand command = validCommand().build();

            // When: PUT request to update achievement owned by different user
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Returns 401 Unauthorized with not authorized message
            result.andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(containsString(ERROR_NOT_AUTHORIZED)))
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"));

            // Verify no update occurred
            verify(userPSQLProvider).findByKey(VALID_USER_KEY, UserKto.class);
            verify(achievementPSQLProvider).findAchievementByKey(Achievement.class, VALID_ACHIEVEMENT_KEY);
            verify(achievementPSQLProvider, never()).save(any());
        }
    }

    @Nested
    @DisplayName("AC6: JSR-303 Validation Failures")
    class ValidationFailures {

        @Test
        @DisplayName("AC6.1: Should return 400 Bad Request when required fields are missing")
        void missingRequiredFields() throws Exception {
            // Given: Command with missing title
            UpdateAchievementCommand command = new UpdateAchievementCommand();
            command.setDescription("Missing title field");
            command.setSkills(new String[]{"int"});

            // When: PUT request with invalid command
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Validation error with field requirement message
            assertValidationError(result, ERROR_TITLE_REQUIRED);
            verifyNoRepositoryInteractions();
        }

        @Test
        @DisplayName("AC6.2: Should return 400 Bad Request when title exceeds 200 characters")
        void titleTooLong() throws Exception {
            // Given: Command with excessively long title
            String longTitle = "This is a very long title that exceeds the maximum allowed length of 200 characters and should trigger " +
                              "a validation error because it contains way too many characters and goes beyond the specified limit and continues even more...";
            
            UpdateAchievementCommand command = validCommand()
                .title(longTitle)
                .build();

            // When: PUT request with long title
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Validation error about title length
            assertValidationError(result, ERROR_TITLE_MAX_LENGTH);
            verifyNoRepositoryInteractions();
        }

        @Test
        @DisplayName("AC6.3: Should return 400 Bad Request when title is null")
        void nullTitle() throws Exception {
            // Given: Command with null title
            UpdateAchievementCommand command = validCommand()
                .title(null)
                .build();

            // When: PUT request with null title
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Validation error about missing title
            assertValidationError(result, ERROR_TITLE_REQUIRED);
            verifyNoRepositoryInteractions();
        }
    }

    @Nested
    @DisplayName("AC7: Skills Validation Failures")
    class SkillsValidation {

        @Test
        @DisplayName("AC7.1: Should return 400 Bad Request when skills are invalid")
        void invalidSkills() throws Exception {
            // Given: Command with invalid skills
            UpdateAchievementCommand command = validCommand()
                .skills("magic", "invalid", "str")
                .build();

            // When: PUT request with invalid skills
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Validation error about invalid skills
            result.andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.data").value(containsString(ERROR_INVALID_SKILL)));

            // Verify no achievement was updated
            verify(achievementPSQLProvider, never()).save(any());
        }

        @Test
        @DisplayName("AC7.2: Should return 400 Bad Request when no skills provided (empty array)")
        void noSkillsProvided() throws Exception {
            // Given: Command with empty skills array
            UpdateAchievementCommand command = validCommand()
                .skills(new String[]{})
                .build();

            // When: PUT request with empty skills
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Validation error about skills requirement
            assertValidationError(result, ERROR_NO_SKILLS);
            verifyNoRepositoryInteractions();
        }

        @Test
        @DisplayName("AC7.3: Should return 400 Bad Request when more than 7 skills provided")
        void tooManySkills() throws Exception {
            // Given: Command with more than 7 skills
            UpdateAchievementCommand command = validCommand()
                .skills("str", "dex", "con", "wis", "int", "cha", "luc", "extra")
                .build();

            // When: PUT request with too many skills
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Validation error about skills count
            result.andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.data").exists());

            verifyNoRepositoryInteractions();
        }
    }

    @Nested
    @DisplayName("AC8: Achievement Visibility Settings")
    class VisibilitySettings {

        @Test
        @DisplayName("AC8.1: Should allow update to public visibility")
        void updateToPublic() throws Exception {
            // Given: Mock successful update
            setupSuccessfulUpdateMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
            
            UpdateAchievementCommand command = validCommand()
                .title("Public Achievement")
                .description("This achievement is public")
                .isPublic(true)
                .build();

            // When: PUT request with public visibility
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Achievement updated successfully
            assertSuccessfulUpdate(result, VALID_ACHIEVEMENT_KEY);
            verifySuccessfulUpdateInteractions(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
        }

        @Test
        @DisplayName("AC8.2: Should allow update to private visibility")
        void updateToPrivate() throws Exception {
            // Given: Mock successful update
            setupSuccessfulUpdateMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
            
            UpdateAchievementCommand command = validCommand()
                .title("Private Achievement")
                .description("This achievement is private")
                .isPublic(false)
                .build();

            // When: PUT request with private visibility
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Achievement updated successfully
            assertSuccessfulUpdate(result, VALID_ACHIEVEMENT_KEY);
            verifySuccessfulUpdateInteractions(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
        }

        @Test
        @DisplayName("AC8.3: Should use default visibility (true) when not specified")
        void defaultVisibility() throws Exception {
            // Given: Mock successful update
            setupSuccessfulUpdateMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
            
            UpdateAchievementCommand command = new CommandBuilder()
                .title("Default Visibility Achievement")
                .description("This achievement uses default visibility")
                .skills("int")
                // isPublic not set - should default to true
                .build();

            // When: PUT request without visibility specified
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Achievement updated successfully with default visibility
            assertSuccessfulUpdate(result, VALID_ACHIEVEMENT_KEY);
            verifySuccessfulUpdateInteractions(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
        }
    }

    @Nested
    @DisplayName("AC9: Edge Cases and Boundary Conditions")
    class EdgeCases {

        @Test
        @DisplayName("AC9.1: Should normalize skills (trim and lowercase)")
        void skillNormalization() throws Exception {
            // Given: Mock successful update
            setupSuccessfulUpdateMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
            
            UpdateAchievementCommand command = validCommand()
                .title("Case Test Achievement")
                .description("Testing case sensitivity")
                .skills("STR", "Dex", "INT") // Mixed case
                .build();

            // When: PUT request with mixed case skills
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Skills normalized and achievement updated successfully
            assertSuccessfulUpdate(result, VALID_ACHIEVEMENT_KEY);
        }

        @Test
        @DisplayName("AC9.2: Should handle skills with whitespace")
        void skillsWithWhitespace() throws Exception {
            // Given: Mock successful update
            setupSuccessfulUpdateMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
            
            UpdateAchievementCommand command = validCommand()
                .title("Whitespace Test Achievement")
                .description("Testing whitespace handling")
                .skills(" str ", "  dex", "int  ") // Skills with whitespace
                .build();

            // When: PUT request with whitespace in skills
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Whitespace trimmed and achievement updated successfully
            assertSuccessfulUpdate(result, VALID_ACHIEVEMENT_KEY);
        }
    }

    @Nested
    @DisplayName("AC10: Database and System Errors")
    class DatabaseAndSystemErrors {

        @Test
        @DisplayName("AC10.1: Should return 400 with friendly error message when update operation fails")
        void updateOperationFailure() throws Exception {
            // Given: Mock update operation failure
            when(userPSQLProvider.findByKey(VALID_USER_KEY, UserKto.class)).thenReturn(mockUser);
            when(achievementPSQLProvider.findAchievementByKey(Achievement.class, VALID_ACHIEVEMENT_KEY)).thenReturn(mockAchievement);
            when(achievementPSQLProvider.save(any(Achievement.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

            UpdateAchievementCommand command = validCommand().build();

            // When: PUT request that triggers database error
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Bad request with friendly message about update failure
            result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));

            verify(userPSQLProvider).findByKey(VALID_USER_KEY, UserKto.class);
            verify(achievementPSQLProvider).findAchievementByKey(Achievement.class, VALID_ACHIEVEMENT_KEY);
        }
    }

    @Nested
    @DisplayName("AC11: Request Flow and Handler Execution")
    class RequestFlowAndHandlerExecution {

        @Test
        @DisplayName("AC11.1: Should execute complete success flow with all validations in correct order")
        void completeSuccessFlow() throws Exception {
            // Given: Complete valid update request
            setupSuccessfulUpdateMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
            
            UpdateAchievementCommand command = validCommand()
                .title("Complete Flow Test")
                .description("Testing complete request flow execution")
                .skills("int", "wis")
                .isPublic(true)
                .build();

            // When: PUT request with complete valid data
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Complete flow executes successfully
            // 1. Authentication validated
            // 2. Command validated (JSR-303 and custom)
            // 3. User lookup performed
            // 4. Achievement lookup performed
            // 5. Ownership verification performed
            // 6. Achievement updated
            // 7. Success response returned
            assertSuccessfulUpdate(result, VALID_ACHIEVEMENT_KEY);
            verifySuccessfulUpdateInteractions(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
        }

        @Test
        @DisplayName("AC11.2: Should handle exceptions with proper structured error response")
        void exceptionHandlingFlow() throws Exception {
            // Given: Invalid command that will trigger validation
            UpdateAchievementCommand command = validCommand()
                .title("") // Empty title triggers validation
                .build();

            // When: PUT request with invalid data
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Structured error response returned
            result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));
            
            verifyNoRepositoryInteractions();
        }

        @Test
        @DisplayName("AC11.3: Should execute validation sequence before database operations")
        void validationSequence() throws Exception {
            // Given: Invalid command with empty title
            UpdateAchievementCommand command = new UpdateAchievementCommand();
            command.setTitle("");
            command.setDescription("Valid description");
            command.setSkills(new String[]{"invalid"});

            // When: PUT request with validation errors
            ResultActions result = performUpdateAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY, command);

            // Then: Validation fails before any database operations
            result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));

            // Verify no database operations were attempted
            verify(userPSQLProvider, never()).findByKey(any(), any());
            verifyNoRepositoryInteractions();
        }
    }
}
