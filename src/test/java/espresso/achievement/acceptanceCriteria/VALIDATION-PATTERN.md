# Response Validation Pattern

## Overview
All acceptance criteria tests follow a **complete validation pattern** that ensures comprehensive verification of API responses.

## Minimal Validation Requirements

Every acceptance criteria test **MUST** validate at minimum:

### ✅ For Success Responses (2xx)
1. **Content-Type**: Verify `application/json`
2. **HTTP Status Code**: Verify correct status (200, 201, etc.)
3. **Success Flag**: Verify `$.success` is `true`
4. **Response Data**: Verify expected data is present and correct
5. **HTTP Status in Response**: Verify `$.httpStatus` matches actual HTTP status

### ✅ For Bad Request Errors (400)
1. **Content-Type**: Verify `application/json`
2. **HTTP Status Code**: Verify `400 Bad Request`
3. **Success Flag**: Verify `$.success` is `false`
4. **Error Message**: Verify `$.data` contains descriptive error message
5. **HTTP Status in Response**: Verify `$.httpStatus` is `BAD_REQUEST`

### ✅ For Authentication/Authorization Errors (401/403)
1. **HTTP Status Code**: Verify `401 Unauthorized` or `403 Forbidden`
2. **Note**: JSON body may not be present for 401 responses (Spring Security default)

### ✅ For Not Found Errors (404)
1. **Content-Type**: Verify `application/json`
2. **HTTP Status Code**: Verify `404 Not Found`
3. **Success Flag**: Verify `$.success` is `false`
4. **Error Message**: Verify `$.data` indicates what resource was not found
5. **HTTP Status in Response**: Verify `$.httpStatus` is `NOT_FOUND`

### ✅ For Server Errors (500)
1. **Content-Type**: Verify `application/json`
2. **HTTP Status Code**: Verify `500 Internal Server Error`
3. **Success Flag**: Verify `$.success` is `false`
4. **Friendly Error Message**: Verify `$.data` contains user-friendly error (not stack trace)
5. **HTTP Status in Response**: Verify `$.httpStatus` is `INTERNAL_SERVER_ERROR`

## Implementation Patterns

### Pattern 1: Success Response Validation

```java
@Test
void successfulCreation() throws Exception {
    // Given
    setupSuccessfulCreationMocks(USER_KEY, ENTITY_KEY);
    CreateAchivementCommand command = validCommand().build();

    // When
    ResultActions result = performCreateAchievement(USER_KEY, command);

    // Then
    assertSuccessfulCreation(result, ENTITY_KEY);
}

// Helper method
private void assertSuccessfulCreation(ResultActions result, String expectedKey) throws Exception {
    result.andExpect(status().isCreated())                          // HTTP 201
        .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // JSON response
        .andExpect(jsonPath("$.success").value(true))                // Success flag
        .andExpect(jsonPath("$.data.entityKey").value(expectedKey))  // Expected data
        .andExpect(jsonPath("$.httpStatus").value("CREATED"));       // Status in response
}
```

### Pattern 2: Validation Error Response

```java
@Test
void validationFailure() throws Exception {
    // Given
    CreateAchivementCommand command = validCommand()
        .title("")  // Invalid: blank title
        .build();

    // When
    ResultActions result = performCreateAchievement(USER_KEY, command);

    // Then
    assertValidationError(result, "TITLE MUST BE PROVIDED");
}

// Helper method
private void assertValidationError(ResultActions result, String errorFragment) throws Exception {
    result.andExpect(status().isBadRequest())                        // HTTP 400
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // JSON response
        .andExpect(jsonPath("$.success").value(false))                 // Failure flag
        .andExpect(jsonPath("$.data").value(containsString(errorFragment))) // Error message
        .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));     // Status in response
}
```

### Pattern 3: Authentication Error

```java
@Test
void authenticationFailure() throws Exception {
    // Given
    CreateAchivementCommand command = validCommand().build();

    // When
    ResultActions result = performUnauthenticatedRequest(command);

    // Then
    assertUnauthorized(result);
}

// Helper method
private void assertUnauthorized(ResultActions result) throws Exception {
    result.andExpect(status().isUnauthorized());  // HTTP 401
    // Note: JSON body may not be present for 401 responses
}
```

### Pattern 4: System Error Response

```java
@Test
void systemError() throws Exception {
    // Given
    setupDatabaseFailureMock(USER_KEY);
    CreateAchivementCommand command = validCommand().build();

    // When
    ResultActions result = performCreateAchievement(USER_KEY, command);

    // Then
    result.andExpect(status().isInternalServerError())              // HTTP 500
        .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // JSON response
        .andExpect(jsonPath("$.success").value(false))               // Failure flag
        .andExpect(jsonPath("$.data").exists())                      // Friendly error message
        .andExpect(jsonPath("$.httpStatus").value("INTERNAL_SERVER_ERROR")); // Status
}
```

## Response Structure Examples

### Success Response (201 Created)
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "httpStatus": "CREATED"
}
```

### Validation Error Response (400 Bad Request)
```json
{
  "success": false,
  "data": "LOCALIZE: A TITLE MUST BE PROVIDED",
  "httpStatus": "BAD_REQUEST"
}
```

### User Not Found Response (404 Not Found)
```json
{
  "success": false,
  "data": "User not found",
  "httpStatus": "NOT_FOUND"
}
```

### System Error Response (500 Internal Server Error)
```json
{
  "success": false,
  "data": "An unexpected error occurred. Please try again later.",
  "httpStatus": "INTERNAL_SERVER_ERROR"
}
```

## Benefits of Complete Validation Pattern

1. **API Contract Enforcement**: Ensures consistent response structure across all endpoints
2. **Breaking Change Detection**: Tests fail immediately if response format changes
3. **Client Reliability**: Clients can depend on consistent response structure
4. **Error Diagnosis**: Complete validation helps identify exactly what failed
5. **Documentation**: Tests serve as executable documentation of API behavior

## Coverage Matrix

| Acceptance Criteria | Content-Type | HTTP Status | Success Flag | Response Data | HTTP Status Field |
|-------------------|--------------|-------------|--------------|---------------|-------------------|
| AC1 (Success) | ✅ | ✅ | ✅ | ✅ | ✅ |
| AC2 (Auth) | ⚠️ N/A | ✅ | ⚠️ N/A | ⚠️ N/A | ⚠️ N/A |
| AC3 (Not Found) | ✅ | ✅ | ✅ | ✅ | ✅ |
| AC4 (Validation) | ✅ | ✅ | ✅ | ✅ | ✅ |
| AC5 (Date) | ✅ | ✅ | ✅ | ✅ | ✅ |
| AC6 (Skills) | ✅ | ✅ | ✅ | ✅ | ✅ |
| AC7 (Visibility) | ✅ | ✅ | ✅ | ✅ | ✅ |
| AC8 (DB Errors) | ✅ | ✅ | ✅ | ✅ | ✅ |
| AC9 (Edge Cases) | ✅ | ✅ | ✅ | ✅ | ✅ |
| AC10 (Flow) | ✅ | ✅ | ✅ | ✅ | ✅ |

**Legend**: ✅ = Validated, ⚠️ = May not apply (Spring Security defaults for 401)

## Checklist for New Tests

When creating new acceptance criteria tests, ensure:

- [ ] Content-Type is validated (for JSON responses)
- [ ] HTTP status code is validated
- [ ] Success flag (`$.success`) is validated
- [ ] Response data is validated (specific values or structure)
- [ ] HTTP status in response (`$.httpStatus`) is validated
- [ ] Helper methods are used to reduce code duplication
- [ ] Given/When/Then comments clearly explain test scenario
- [ ] Test name describes the expected behavior
- [ ] Repository interactions are verified (when applicable)
