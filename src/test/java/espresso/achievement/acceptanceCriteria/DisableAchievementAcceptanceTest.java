package espresso.achievement.acceptanceCriteria;

import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.contracts.IAchievementRepository;
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
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Acceptance Criteria Tests for Disable Achievement Endpoint
 * Based on: src/main/java/espresso/achievement/service/acceptanceCriteria/disable-achievement-ac.md
 * 
 * Testing Strategy:
 * - Uses @SpringBootTest to load full application context
 * - Uses @AutoConfigureMockMvc for API layer testing with MockMvc
 * - Mocks PSQLProvider to avoid database dependencies
 * - Tests from API layer down through domain logic
 * - Focuses on behavior coverage over code coverage
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Disable Achievement - Acceptance Criteria")
class DisableAchievementAcceptanceTest {

    // ===== Constants =====
    private static final String API_ENDPOINT = "/api/cmd/achievement";
    private static final String VALID_USER_KEY = "ABC1234";
    private static final String INVALID_USER_KEY = "XYZ9999";
    private static final String OTHER_USER_KEY = "OTHER12";
    private static final String VALID_ACHIEVEMENT_KEY = "8NctRKY";
    private static final String MISSING_ACHIEVEMENT_KEY = "MISSING";
    
    // Error messages
    private static final String ERROR_USER_NOT_FOUND = "User not found";
    private static final String ERROR_ACHIEVEMENT_NOT_FOUND = "ACHIEVEMENT NOT FOUND";
    private static final String ERROR_NOT_AUTHORIZED = "USER IS NOT AUTHORIZED TO DISABLE THIS ACHIEVEMENT";
    private static final String ERROR_KEY_LENGTH = "7 CHARACTERS";
    
    // ===== Dependencies =====
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock bean for Achievement repository.
     * Note: @MockBean is deprecated since Spring Boot 3.4.0 and marked for removal.
     * Will be replaced with @MockitoBean (requires spring-boot-testcontainers dependency).
     * Suppressing warning until dependency is added.
     */
    @SuppressWarnings("removal")
    @MockBean
    private IAchievementRepository achievementRepository;

    /**
     * Mock bean for User repository.
     * Note: @MockBean is deprecated since Spring Boot 3.4.0 and marked for removal.
     */
    @SuppressWarnings("removal")
    @MockBean
    private UserPSQLProvider userPSQLProvider;

    private UserKto mockUser;
    private Achievement mockEnabledAchievement;
    private Achievement mockDisabledAchievement;

    // ===== Setup =====
    @BeforeEach
    void setUp() {
        mockUser = createMockUser(VALID_USER_KEY);
        mockEnabledAchievement = createMockAchievement(VALID_ACHIEVEMENT_KEY, mockUser, true);
        mockDisabledAchievement = createMockAchievement(VALID_ACHIEVEMENT_KEY, mockUser, false);
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
     * Creates a mock Achievement with the specified key and owner.
     * @param achievementKey The achievement key
     * @param owner The owner UserKto
     * @param enabled Whether the achievement is enabled
     */
    private Achievement createMockAchievement(String achievementKey, UserKto owner, boolean enabled) {
        Achievement achievement = new Achievement();
        achievement.setEntityKey(achievementKey);
        achievement.setUser(User.fromKto(owner));
        achievement.setTitle("Test Achievement");
        achievement.setDescription("Test Description");
        achievement.setCompletedDate(new Date());
        achievement.setSkills(List.of("Java", "Spring"));
        achievement.setEnabled(enabled);
        return achievement;
    }

    // ===== Helper Methods: Mocking Behaviors =====

    /**
     * Sets up mocks for successful disable operation.
     */
    private void setupSuccessfulDisableMocks() {
        when(userPSQLProvider.findByKey(VALID_USER_KEY, UserKto.class)).thenReturn(mockUser);
        when(achievementRepository.getAchievementByKey(Achievement.class, VALID_ACHIEVEMENT_KEY)).thenReturn(mockEnabledAchievement);
        when(achievementRepository.update(any(Achievement.class))).thenReturn(mockEnabledAchievement);
    }

    /**
     * Sets up mocks for already disabled achievement scenario.
     */
    private void setupAlreadyDisabledMocks() {
        when(userPSQLProvider.findByKey(VALID_USER_KEY, UserKto.class)).thenReturn(mockUser);
        when(achievementRepository.getAchievementByKey(Achievement.class, VALID_ACHIEVEMENT_KEY)).thenReturn(mockDisabledAchievement);
    }

    /**
     * Sets up mocks for user not found scenario.
     */
    private void setupUserNotFoundMocks() {
        when(userPSQLProvider.findByKey(INVALID_USER_KEY, UserKto.class)).thenReturn(null);
    }

    /**
     * Sets up mocks for achievement not found scenario.
     */
    private void setupAchievementNotFoundMocks() {
        when(userPSQLProvider.findByKey(VALID_USER_KEY, UserKto.class)).thenReturn(mockUser);
        when(achievementRepository.getAchievementByKey(Achievement.class, MISSING_ACHIEVEMENT_KEY)).thenReturn(null);
    }

    /**
     * Sets up mocks for unauthorized access (different owner) scenario.
     */
    private void setupUnauthorizedMocks() {
        UserKto otherUser = createMockUser(OTHER_USER_KEY);
        Achievement otherUserAchievement = createMockAchievement(VALID_ACHIEVEMENT_KEY, otherUser, true);
        
        when(userPSQLProvider.findByKey(VALID_USER_KEY, UserKto.class)).thenReturn(mockUser);
        when(achievementRepository.getAchievementByKey(Achievement.class, VALID_ACHIEVEMENT_KEY)).thenReturn(otherUserAchievement);
    }

    // ===== Helper Methods: Request Execution =====

    /**
     * Performs a PATCH request to disable an achievement with JWT authentication.
     */
    private ResultActions performDisableAchievement(String achievementKey, String userKey) throws Exception {
        return mockMvc.perform(patch(API_ENDPOINT + "/{key}/disable", achievementKey)
                .with(withJwtAuth(userKey))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    /**
     * Performs a PATCH request without authentication.
     */
    private ResultActions performDisableAchievementWithoutAuth(String achievementKey) throws Exception {
        return mockMvc.perform(patch(API_ENDPOINT + "/{key}/disable", achievementKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    /**
     * Creates a JWT authentication post-processor with the specified user key.
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

    // ===== Helper Methods: Assertions =====

    /**
     * Asserts successful disable response format.
     */
    private void assertSuccessfulDisableResponse(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.success", is(true)))
              .andExpect(jsonPath("$.data.entityKey", is(VALID_ACHIEVEMENT_KEY)))
              .andExpect(jsonPath("$.httpStatus", is("OK")));
    }

    /**
     * Asserts error response format.
     */
    private void assertErrorResponse(ResultActions result, int expectedStatus, String expectedMessage) throws Exception {
        result.andExpect(status().is(expectedStatus))
              .andExpect(jsonPath("$.success", is(false)))
              .andExpect(jsonPath("$.data", containsStringIgnoringCase(expectedMessage)))
              .andExpect(jsonPath("$.httpStatus").exists());
    }

    // ===== Acceptance Criteria Tests =====

    @Nested
    @DisplayName("AC1: Successful Achievement Disable")
    class AC1_SuccessfulDisable {

        @Test
        @DisplayName("Should disable enabled achievement and return 200 OK with entity key")
        void shouldDisableEnabledAchievement() throws Exception {
            // Given
            setupSuccessfulDisableMocks();

            // When
            ResultActions result = performDisableAchievement(VALID_ACHIEVEMENT_KEY, VALID_USER_KEY);

            // Then
            assertSuccessfulDisableResponse(result);
            
            // Verify achievement.disable() was called through update
            verify(achievementRepository).update(argThat(achievement -> 
                !achievement.isEnabled() && 
                achievement.getEntityKey().equals(VALID_ACHIEVEMENT_KEY)
            ));
        }
    }

    @Nested
    @DisplayName("AC2: Invalid Achievement Key")
    class AC2_InvalidKey {

        @Test
        @DisplayName("Should return 400 BAD REQUEST for achievement key too short")
        void shouldRejectKeyTooShort() throws Exception {
            // Given
            String shortKey = "ABC123"; // Only 6 characters

            // When
            ResultActions result = performDisableAchievement(shortKey, VALID_USER_KEY);

            // Then
            result.andExpect(status().isBadRequest())
                  .andExpect(jsonPath("$.success", is(false)))
                  .andExpect(jsonPath("$.data", containsStringIgnoringCase(ERROR_KEY_LENGTH)))
                  .andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")));
        }

        @Test
        @DisplayName("Should return 400 BAD REQUEST for achievement key too long")
        void shouldRejectKeyTooLong() throws Exception {
            // Given
            String longKey = "ABC12345"; // 8 characters

            // When
            ResultActions result = performDisableAchievement(longKey, VALID_USER_KEY);

            // Then
            result.andExpect(status().isBadRequest())
                  .andExpect(jsonPath("$.success", is(false)))
                  .andExpect(jsonPath("$.data", containsStringIgnoringCase(ERROR_KEY_LENGTH)))
                  .andExpect(jsonPath("$.httpStatus", is("BAD_REQUEST")));
        }
    }

    @Nested
    @DisplayName("AC3: Achievement Not Found")
    class AC3_AchievementNotFound {

        @Test
        @DisplayName("Should return 404 NOT FOUND when achievement doesn't exist")
        void shouldReturn404WhenAchievementNotFound() throws Exception {
            // Given
            setupAchievementNotFoundMocks();

            // When
            ResultActions result = performDisableAchievement(MISSING_ACHIEVEMENT_KEY, VALID_USER_KEY);

            // Then
            assertErrorResponse(result, 404, ERROR_ACHIEVEMENT_NOT_FOUND);
            
            // Verify no update was attempted
            verify(achievementRepository, never()).update(any(Achievement.class));
        }
    }

    @Nested
    @DisplayName("AC4: User Not Found")
    class AC4_UserNotFound {

        @Test
        @DisplayName("Should return 404 NOT FOUND when user doesn't exist")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            setupUserNotFoundMocks();

            // When
            ResultActions result = performDisableAchievement(VALID_ACHIEVEMENT_KEY, INVALID_USER_KEY);

            // Then
            assertErrorResponse(result, 404, ERROR_USER_NOT_FOUND);
            
            // Verify no update was attempted
            verify(achievementRepository, never()).update(any(Achievement.class));
        }
    }

    @Nested
    @DisplayName("AC5: Unauthorized Access - Not Owner")
    class AC5_NotOwner {

        @Test
        @DisplayName("Should return 401 UNAUTHORIZED when user is not the achievement owner")
        void shouldReturn401WhenNotOwner() throws Exception {
            // Given
            setupUnauthorizedMocks();

            // When
            ResultActions result = performDisableAchievement(VALID_ACHIEVEMENT_KEY, VALID_USER_KEY);

            // Then
            assertErrorResponse(result, 401, ERROR_NOT_AUTHORIZED);
            
            // Verify no update was attempted
            verify(achievementRepository, never()).update(any(Achievement.class));
        }
    }

    @Nested
    @DisplayName("AC6: Missing Authentication")
    class AC6_MissingAuth {

        @Test
        @DisplayName("Should return 401 UNAUTHORIZED when no JWT token is provided")
        void shouldReturn401WhenNoAuth() throws Exception {
            // When
            ResultActions result = performDisableAchievementWithoutAuth(VALID_ACHIEVEMENT_KEY);

            // Then
            result.andExpect(status().isUnauthorized());
            
            // Verify no repository calls were made
            verify(userPSQLProvider, never()).findByKey(anyString(), eq(UserKto.class));
            verify(achievementRepository, never()).update(any(Achievement.class));
        }
    }

    @Nested
    @DisplayName("AC7: Already Disabled Achievement (Idempotent)")
    class AC7_AlreadyDisabled {

        @Test
        @DisplayName("Should return 200 OK when achievement is already disabled (idempotent operation)")
        void shouldReturn200ForAlreadyDisabled() throws Exception {
            // Given
            setupAlreadyDisabledMocks();

            // When
            ResultActions result = performDisableAchievement(VALID_ACHIEVEMENT_KEY, VALID_USER_KEY);

            // Then
            assertSuccessfulDisableResponse(result);
            
            // Verify no update was attempted (already disabled)
            verify(achievementRepository, never()).update(any(Achievement.class));
        }
    }

    @Nested
    @DisplayName("AC8: Internal Server Error Handling")
    class AC8_InternalServerError {

        @Test
        @DisplayName("Should return 500 INTERNAL SERVER ERROR when unexpected error occurs")
        void shouldReturn500OnUnexpectedError() throws Exception {
            // Given
            when(userPSQLProvider.findByKey(VALID_USER_KEY, UserKto.class)).thenReturn(mockUser);
            when(achievementRepository.getAchievementByKey(Achievement.class, VALID_ACHIEVEMENT_KEY)).thenReturn(mockEnabledAchievement);
            doThrow(new RuntimeException("Database connection error")).when(achievementRepository).update(any(Achievement.class));

            // When
            ResultActions result = performDisableAchievement(VALID_ACHIEVEMENT_KEY, VALID_USER_KEY);

            // Then
            result.andExpect(status().isInternalServerError())
                  .andExpect(jsonPath("$.success", is(false)))
                  .andExpect(jsonPath("$.httpStatus", is("INTERNAL_SERVER_ERROR")));
        }
    }
}
