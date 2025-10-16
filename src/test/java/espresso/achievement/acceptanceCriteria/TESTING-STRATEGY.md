# Acceptance Criteria Testing Strategy

## Overview

This document outlines the testing strategy implemented for the Espresso Achievement application, focusing on acceptance criteria testing rather than traditional unit testing.

## Testing Philosophy

### Why Acceptance Criteria First?

1. **Behavior Coverage Over Code Coverage**: We prioritize testing that the application behaves correctly according to user stories and business requirements.

2. **Stable Testing Foundation**: By waiting until patterns and architecture stabilized, we avoid the churn of constantly updating unit tests during rapid development.

3. **End-to-End Validation**: Acceptance tests validate entire flows from API layer through domain logic, catching integration issues that unit tests might miss.

4. **Living Documentation**: Acceptance tests serve as executable specifications, directly mapping to the acceptance criteria documented in markdown files.

## Testing Approach

### Framework: JUnit 5 + Spring Boot Test

We use:
- **JUnit 5** - Modern testing framework with excellent Spring support
- **Spring Boot Test** - `@SpringBootTest` for full application context
- **MockMvc** - For testing API layer without running actual server
- **Mockito** - For mocking infrastructure dependencies (PSQLProvider, S3Provider)

### Testing Strategy

```
┌─────────────────────────────────────────┐
│         API Layer (MockMvc)             │
├─────────────────────────────────────────┤
│         Application Layer               │
│    (Command/Query Handlers)             │
├─────────────────────────────────────────┤
│         Domain Layer                    │
│    (Entities, Value Objects)            │
├─────────────────────────────────────────┤
│     Infrastructure Layer (MOCKED)       │
│    PSQLProvider, S3Provider             │
└─────────────────────────────────────────┘
```

**What We Test:**
- API endpoints (Controllers)
- Command/Query handlers
- Domain logic
- Validation rules
- Error handling

**What We Mock:**
- Database repositories (PSQLProvider interfaces)
- External storage (S3Provider)
- External services (Azure AI, etc.)

### Benefits of This Approach

1. **No Docker Required**: Tests run without TestContainers, making them faster and easier to run locally
2. **Full Context**: Uses real Spring Boot application context, catching configuration issues
3. **Fast Feedback**: Mocked infrastructure means tests run quickly
4. **Flexible**: Can switch to real database/storage later if needed
5. **Reusable Pattern**: Same pattern applies to all user stories

## Test Structure

### Directory Organization

```
src/test/java/espresso/
└── achievement/
    └── acceptanceCriteria/
        ├── CreateAchievementAcceptanceTest.java
        ├── UpdateAchievementAcceptanceTest.java (future)
        ├── DeleteAchievementAcceptanceTest.java (future)
        └── ... (other user stories)
```

### Test Class Structure

Each acceptance test follows this pattern:

```java
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Feature Name - Acceptance Criteria")
class FeatureAcceptanceTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PSQLProvider psqlProvider;
    
    @Nested
    @DisplayName("AC1: Scenario Name")
    class ScenarioTests {
        
        @Test
        @WithMockUser
        @DisplayName("Given X When Y Then Z")
        void testScenario() {
            // Arrange
            // Act
            // Assert
        }
    }
}
```

### Naming Conventions

- **Test Classes**: `{Feature}AcceptanceTest.java`
- **Nested Classes**: Match acceptance criteria (AC1, AC2, etc.)
- **Test Methods**: Descriptive names using `@DisplayName`
- **Given-When-Then**: Structure test logic clearly

## Running Tests

### Run All Tests
```bash
mvnw test
```

### Run Specific Test Class
```bash
mvnw test -Dtest=CreateAchievementAcceptanceTest
```

### Run Specific Test Method
```bash
mvnw test -Dtest=CreateAchievementAcceptanceTest#createAchievementSuccess
```

### Run from IDE
- Right-click on test class or method
- Select "Run Test" or "Debug Test"
- VS Code: Use Testing view (beaker icon)

## Test Coverage

### Current Coverage

| User Story | Status | Test File |
|------------|--------|-----------|
| Create Achievement | ✅ Complete | CreateAchievementAcceptanceTest.java |
| Update Achievement | ⏳ Planned | - |
| Delete Achievement | ⏳ Planned | - |
| Upload Media | ⏳ Planned | - |
| Add Comment | ⏳ Planned | - |

### Coverage Goals

We focus on:
- ✅ Happy path scenarios
- ✅ Validation failures
- ✅ Authentication/Authorization
- ✅ Edge cases and boundaries
- ✅ Error handling

We intentionally skip:
- ❌ Low-level utility functions (unless complex)
- ❌ Simple getters/setters
- ❌ Framework code
- ❌ Configuration classes (unless critical logic)

## Extending the Test Suite

### Adding a New Acceptance Test

1. **Reference the Acceptance Criteria Document**
   - Located in: `src/main/java/espresso/{domain}/service/acceptanceCriteria/{feature}-ac.md`
   - This is your specification

2. **Create Test Class**
   ```java
   @SpringBootTest
   @AutoConfigureMockMvc
   @DisplayName("{Feature} - Acceptance Criteria")
   class {Feature}AcceptanceTest {
       // Setup mocks and test methods
   }
   ```

3. **Mock Infrastructure Dependencies**
   ```java
   @MockBean
   private SomePSQLProvider psqlProvider;
   
   @MockBean
   private SomeS3Provider s3Provider;
   ```

4. **Organize by Acceptance Criteria**
   ```java
   @Nested
   @DisplayName("AC1: First Scenario")
   class FirstScenario {
       @Test
       void testCase() { }
   }
   ```

5. **Write Tests Following Given-When-Then**
   ```java
   @Test
   @WithMockUser(username = "testuser")
   @DisplayName("Should succeed when given valid data")
   void testSuccess() throws Exception {
       // Given - Setup preconditions and mocks
       when(repository.findByKey(any(), eq("KEY123")))
           .thenReturn(mockEntity);
       
       // When - Perform the action
       mockMvc.perform(post("/api/endpoint")
               .contentType(MediaType.APPLICATION_JSON)
               .content(json))
       
       // Then - Verify results
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.success").value(true));
       
       verify(repository).save(any());
   }
   ```

## Future Enhancements

As the project evolves, consider:

1. **Integration Tests**: Add tests with real database (TestContainers)
2. **Performance Tests**: Test endpoints under load
3. **Contract Tests**: Verify API contracts with consumers
4. **E2E Tests**: Full end-to-end tests with UI
5. **Test Data Builders**: Create fluent builders for complex test data

## Best Practices

### DO:
✅ Write tests that match acceptance criteria exactly  
✅ Use descriptive test names with @DisplayName  
✅ Mock external dependencies  
✅ Verify important interactions with mocks  
✅ Test both happy and error paths  
✅ Keep tests independent and idempotent  

### DON'T:
❌ Test framework code  
❌ Test simple getters/setters  
❌ Make tests dependent on execution order  
❌ Use real database/external services (yet)  
❌ Over-mock (mock too deep in the stack)  
❌ Ignore test failures  

## Troubleshooting

### Common Issues

**Problem**: Tests fail with "No qualifying bean of type..."  
**Solution**: Add `@MockBean` for the missing dependency

**Problem**: Authentication issues in tests  
**Solution**: Use `@WithMockUser` annotation or configure test security

**Problem**: Tests pass locally but fail in CI  
**Solution**: Check for environment-specific configurations, ensure mocks are complete

**Problem**: Slow test execution  
**Solution**: Review what's being loaded, consider `@WebMvcTest` for slice testing

## Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Assertions](https://assertj.github.io/doc/)

## Contact

For questions about the testing strategy, please refer to this document or consult the development team.
