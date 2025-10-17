package espresso.achievement.acceptanceCriteria;

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
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Acceptance Criteria Tests for Delete Achievement Endpoint
 * Based on: src/main/java/espresso/achievement/service/acceptanceCriteria/delete-achievement-ac.md
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
@DisplayName("Delete Achievement - Acceptance Criteria")
class DeleteAchievementAcceptanceTest {

    // ===== Constants =====
    private static final String API_ENDPOINT = "/api/cmd/achievement";
    private static final String VALID_USER_KEY = "ABC1234";
    private static final String INVALID_USER_KEY = "XYZ9999";
    private static final String OTHER_USER_KEY = "OTHER12";
    private static final String VALID_ACHIEVEMENT_KEY = "8NctRKY";
    private static final String MISSING_ACHIEVEMENT_KEY = "MISSING";
    
    // Error messages
    private static final String ERROR_USER_NOT_FOUND = "User not found";
    private static final String ERROR_ACHIEVEMENT_NOT_FOUND = "achievement.not.found";
    private static final String ERROR_NOT_AUTHORIZED = "USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT";
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
        achievement.setTitle("Test Achievement");
        achievement.setDescription("Test Description");
        achievement.setCompletedDate(new Date());
        achievement.setSkills(List.of("skill1", "skill2"));
        return achievement;
    }

    // ===== Helper Methods: Mock Setup =====
    
    /**
     * Sets up mocks for successful deletion scenario.
     */
    private void setupSuccessfulDeletionMocks(String userKey, String achievementKey) {
        when(userPSQLProvider.findByKey(userKey, UserKto.class)).thenReturn(mockUser);
        when(achievementPSQLProvider.findAchievementByKey(Achievement.class, achievementKey)).thenReturn(mockAchievement);
        doNothing().when(achievementPSQLProvider).deleteAchievementWithDependencies(any(Achievement.class));
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

    /**
     * Sets up mocks for database error scenario.
     */
    private void setupDatabaseErrorMocks(String userKey, String achievementKey) {
        when(userPSQLProvider.findByKey(userKey, UserKto.class)).thenReturn(mockUser);
        when(achievementPSQLProvider.findAchievementByKey(Achievement.class, achievementKey)).thenReturn(mockAchievement);
        doThrow(new RuntimeException("Database connection failed")).when(achievementPSQLProvider).deleteAchievementWithDependencies(any(Achievement.class));
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
     * Performs DELETE request to delete achievement endpoint.
     */
    private ResultActions performDeleteAchievement(String userKey, String achievementKey) throws Exception {
        return mockMvc.perform(delete(API_ENDPOINT + "/{key}", achievementKey)
            .with(withJwtAuth(userKey))
            .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Performs DELETE request without authentication.
     */
    private ResultActions performUnauthenticatedRequest(String achievementKey) throws Exception {
        return mockMvc.perform(delete(API_ENDPOINT + "/{key}", achievementKey)
            .contentType(MediaType.APPLICATION_JSON));
    }

    // ===== Helper Methods: Assertions =====
    
    /**
     * Asserts successful deletion response (200 OK).
     * Validates: Content-Type, HTTP Status, Success Flag, Response Data, HTTP Status in Response
     */
    private void assertSuccessfulDeletion(ResultActions result, String expectedKey) throws Exception {
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
     * Verifies repository interactions for successful deletion.
     */
    private void verifySuccessfulDeletionInteractions(String userKey, String achievementKey) {
        verify(userPSQLProvider).findByKey(userKey, UserKto.class);
        verify(achievementPSQLProvider).findAchievementByKey(Achievement.class, achievementKey);
        verify(achievementPSQLProvider).deleteAchievementWithDependencies(any(Achievement.class));
    }

    /**
     * Verifies no repository delete interactions occurred.
     */
    private void verifyNoDeleteInteractions() {
        verify(achievementPSQLProvider, never()).deleteAchievementWithDependencies(any());
    }

    // ===== Test Classes =====

    @Nested
    @DisplayName("AC1: Successful Achievement Deletion")
    class SuccessfulDeletion {

        @Test
        @DisplayName("AC1: Should delete achievement with valid data and return 200 OK")
        void deleteAchievementSuccess() throws Exception {
            // Given: Mock successful deletion scenario
            setupSuccessfulDeletionMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);

            // When: DELETE request to delete achievement
            ResultActions result = performDeleteAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);

            // Then: Achievement deleted successfully
            assertSuccessfulDeletion(result, VALID_ACHIEVEMENT_KEY);
            verifySuccessfulDeletionInteractions(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);
        }
    }

    @Nested
    @DisplayName("AC2: Achievement Not Found")
    class AchievementNotFound {

        @Test
        @DisplayName("AC2: Should return 404 Not Found when achievement doesn't exist")
        void achievementNotFound() throws Exception {
            // Given: Achievement does not exist
            setupAchievementNotFoundMocks(VALID_USER_KEY, MISSING_ACHIEVEMENT_KEY);

            // When: DELETE request for non-existent achievement
            ResultActions result = performDeleteAchievement(VALID_USER_KEY, MISSING_ACHIEVEMENT_KEY);

            // Then: Returns 404 Not Found
            assertNotFound(result, ERROR_ACHIEVEMENT_NOT_FOUND);
            verifyNoDeleteInteractions();
            verify(userPSQLProvider).findByKey(VALID_USER_KEY, UserKto.class);
            verify(achievementPSQLProvider).findAchievementByKey(Achievement.class, MISSING_ACHIEVEMENT_KEY);
        }
    }

    @Nested
    @DisplayName("AC3: Authentication Failures")
    class AuthenticationFailures {

        @Test
        @DisplayName("AC3.1: Should return 401 Unauthorized when JWT token is missing")
        void missingJwtToken() throws Exception {
            // Given: Valid achievement key but no JWT token

            // When: DELETE request without authentication
            ResultActions result = performUnauthenticatedRequest(VALID_ACHIEVEMENT_KEY);

            // Then: Unauthorized response
            assertUnauthorized(result);
            verifyNoDeleteInteractions();
        }

        @Test
        @DisplayName("AC3.2: Should return 401 Unauthorized when JWT token is invalid")
        void invalidJwtToken() throws Exception {
            // Given: Invalid JWT token

            // When: DELETE request with invalid token
            ResultActions result = mockMvc.perform(delete(API_ENDPOINT + "/{key}", VALID_ACHIEVEMENT_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer invalid-token"));

            // Then: Unauthorized response
            assertUnauthorized(result);
            verifyNoDeleteInteractions();
        }
    }

    @Nested
    @DisplayName("AC4: User Not Found")
    class UserNotFound {

        @Test
        @DisplayName("AC4: Should return 404 Not Found when user doesn't exist")
        void userNotFound() throws Exception {
            // Given: User does not exist
            setupUserNotFoundMocks(INVALID_USER_KEY);

            // When: DELETE request with non-existent user
            ResultActions result = performDeleteAchievement(INVALID_USER_KEY, VALID_ACHIEVEMENT_KEY);

            // Then: Returns 404 Not Found
            assertNotFound(result, ERROR_USER_NOT_FOUND);
            verifyNoDeleteInteractions();
            verify(userPSQLProvider).findByKey(INVALID_USER_KEY, UserKto.class);
        }
    }

    @Nested
    @DisplayName("AC5: Unauthorized Access (Not Owner)")
    class UnauthorizedAccess {

        @Test
        @DisplayName("AC5: Should return 401 Unauthorized when user doesn't own achievement")
        void notOwner() throws Exception {
            // Given: User exists but doesn't own the achievement
            setupUnauthorizedMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);

            // When: DELETE request from non-owner
            ResultActions result = performDeleteAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);

            // Then: Returns 401 Unauthorized
            result.andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").value(containsString(ERROR_NOT_AUTHORIZED)))
                .andExpect(jsonPath("$.httpStatus").value("UNAUTHORIZED"));

            verifyNoDeleteInteractions();
            verify(userPSQLProvider).findByKey(VALID_USER_KEY, UserKto.class);
            verify(achievementPSQLProvider).findAchievementByKey(Achievement.class, VALID_ACHIEVEMENT_KEY);
        }
    }

    @Nested
    @DisplayName("AC6: JSR-303 Validation Failures")
    class ValidationFailures {

        @Test
        @DisplayName("AC6.1: Should return 400 Bad Request when achievement key is invalid format")
        void invalidAchievementKeyFormat() throws Exception {
            // Given: Achievement key that doesn't meet validation requirements (too short)
            String shortKey = "SHORT";

            // When: DELETE request with invalid key format
            ResultActions result = performDeleteAchievement(VALID_USER_KEY, shortKey);

            // Then: Validation error response
            assertValidationError(result, ERROR_KEY_LENGTH);
            verifyNoDeleteInteractions();
        }

        @Test
        @DisplayName("AC6.2: Should return 400 Bad Request when achievement key is too long")
        void achievementKeyTooLong() throws Exception {
            // Given: Achievement key that is too long
            String longKey = "TOOLONG123";

            // When: DELETE request with key that's too long
            ResultActions result = performDeleteAchievement(VALID_USER_KEY, longKey);

            // Then: Validation error response
            assertValidationError(result, ERROR_KEY_LENGTH);
            verifyNoDeleteInteractions();
        }
    }

    @Nested
    @DisplayName("AC7: Database and System Errors")
    class DatabaseAndSystemErrors {

        @Test
        @DisplayName("AC7: Should return 400 with friendly error message when database deletion fails")
        void databaseDeletionFailure() throws Exception {
            // Given: Valid deletion request but database error occurs
            setupDatabaseErrorMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);

            // When: DELETE request triggers database error
            ResultActions result = performDeleteAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);

            // Then: Returns 400 Bad Request with friendly error message
            // Note: The actual error message is "achievement.update.failed" from AchievementRepository
            assertValidationError(result, "achievement.");
            verify(userPSQLProvider).findByKey(VALID_USER_KEY, UserKto.class);
            verify(achievementPSQLProvider).findAchievementByKey(Achievement.class, VALID_ACHIEVEMENT_KEY);
            verify(achievementPSQLProvider).deleteAchievementWithDependencies(any(Achievement.class));
        }
    }

    @Nested
    @DisplayName("AC8: Proper Deletion Order and Domain Events")
    class DeletionOrderAndDomainEvents {

        @Test
        @DisplayName("AC8: Should execute complete deletion flow with proper order")
        void completeSuccessDeletionFlow() throws Exception {
            // Given: Valid deletion request
            setupSuccessfulDeletionMocks(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);

            // When: DELETE request is executed
            ResultActions result = performDeleteAchievement(VALID_USER_KEY, VALID_ACHIEVEMENT_KEY);

            // Then: Complete flow executes in correct order
            assertSuccessfulDeletion(result, VALID_ACHIEVEMENT_KEY);
            
            // Verify execution order
            var inOrder = inOrder(userPSQLProvider, achievementPSQLProvider);
            inOrder.verify(userPSQLProvider).findByKey(VALID_USER_KEY, UserKto.class);
            inOrder.verify(achievementPSQLProvider).findAchievementByKey(Achievement.class, VALID_ACHIEVEMENT_KEY);
            inOrder.verify(achievementPSQLProvider).deleteAchievementWithDependencies(any(Achievement.class));
        }
    }
}
