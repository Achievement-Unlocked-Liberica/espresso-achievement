# Add Achievement Celebration - Acceptance Criteria

## Feature: Add Achievement Celebration Endpoint

### User Story
As a player, I want to add celebrations to achievements, so that I can show my support and appreciation for other players' accomplishments.

### Endpoint Details
- **Method**: POST
- **URL**: `/api/cmd/achievement/{achievementKey}/celebration`
- **Authentication**: JWT token required (userKey extracted automatically)
- **Content-Type**: application/json
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Celebration Addition
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists in the system with key "ABC1234"
**And** an achievement exists with key "8NctRKY"
**And** the request payload contains valid celebration data:
```json
{
  "count": 3
}
```
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/celebration`
**Then** the system should:
- Extract userKey from JWT token (not from request body)
- Extract achievementKey "8NctRKY" from URL path parameter
- Validate command via CommonCommand.validateCommand()
- Look up user by key "ABC1234" via IUserRepository.findByKey()
- Retrieve achievement by key "8NctRKY" via IAchievementRepository.getAchievementByKey()
- Create new celebration via AchievementCelebration.create(count, achievement, user)
- Add celebration to achievement via achievement.addCelebration(celebration) (raises domain events)
- Save celebration via IAchievementCelebrationRepository.save(celebration)
- Publish domain events via publishDomainEvents(achievement)
- Return HTTP 200 OK
- Return response with achievement entity key:
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "responseType": "SUCCESS"
}
```

### AC2: Authentication Failures

#### AC2.1: Missing JWT Token
**Given** no JWT token is provided in the request
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/celebration`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no celebration added)

#### AC2.2: Invalid or Expired JWT Token
**Given** an invalid or expired JWT token is provided
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/celebration`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no celebration added)

### AC3: User Not Found
**Given** a valid JWT token with userKey "XYZ9999"
**And** no user exists in the system with key "XYZ9999"
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/celebration`
**Then** the system should:
- Extract userKey from JWT token
- Attempt user lookup via IUserRepository.findByKey("XYZ9999", UserKto.class)
- Return HandlerResponse.error("User not found", ResponseType.NOT_FOUND)
- Return HTTP 404 Not Found
- Return error response with correlation ID:
```json
{
  "success": false,
  "error": "User not found",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no celebration added)

### AC4: Achievement Not Found
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** no achievement exists with key "INVALID"
**When** the POST request is made to `/api/cmd/achievement/INVALID/celebration`
**Then** the system should:
- Extract userKey from JWT token
- Look up user successfully
- Attempt achievement lookup via IAchievementRepository.getAchievementByKey("INVALID")
- Find achievement is null
- Return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND)
- Return HTTP 404 Not Found
- Return error response with correlation ID:
```json
{
  "success": false,
  "error": "Achievement not found",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no celebration added)

### AC5: JSR-303 Validation Failures

#### AC5.1: Missing Count Field
**Given** a valid JWT token and existing achievement
**And** the request payload is missing required count field:
```json
{}
```
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/celebration`
**Then** the system should:
- Validate request via CommonCommand.validateCommand()
- Detect missing count field via JSR-303 validation
- Return HTTP 400 Bad Request
- Return validation error response with correlation ID:
```json
{
  "success": false,
  "error": "LOCALIZE: COUNT MUST BE PROVIDED",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no celebration added)

#### AC5.2: Invalid Count Value
**Given** a valid JWT token and existing achievement
**And** the request payload contains invalid count value:
```json
{
  "count": -1
}
```
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/celebration`
**Then** the system should:
- Validate count value via JSR-303 annotations (if present)
- Return HTTP 400 Bad Request
- Return validation error for invalid count value
- Take no action (no celebration added)

### AC6: Domain Events and Message Publishing
**Given** a successful celebration addition
**When** the celebration is processed
**Then** the system should:
- Raise AchievementCelebrationAddedEvent domain event
- Add celebration to achievement's internal celebration list
- Publish domain events to message queue for downstream processing
- Enable other systems to react to celebration events
- Track celebration metrics for analytics

### AC7: Multiple Celebrations from Same User
**Given** a valid JWT token with userKey "ABC1234"
**And** user "ABC1234" has already celebrated achievement "8NctRKY"
**And** the request contains new celebration data:
```json
{
  "count": 2
}
```
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/celebration`
**Then** the system should:
- Allow multiple celebrations from the same user
- Create new celebration entity each time
- Add each celebration independently to the achievement
- Return success response for each celebration

### AC8: Self-Celebration
**Given** a valid JWT token with userKey "ABC1234"
**And** an achievement exists with key "8NctRKY" owned by user "ABC1234"
**And** the request contains celebration data:
```json
{
  "count": 5
}
```
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/celebration`
**Then** the system should:
- Allow users to celebrate their own achievements
- Process the celebration normally
- Create and save the celebration
- Return success response

### AC9: Database and System Errors
**Given** a valid celebration request that should succeed
**And** a database error occurs during the save process
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/celebration`
**Then** the system should:
- Catch exception in AchievementHandlerExceptionPolicy.handleException()
- Return HTTP 500 Internal Server Error
- Return error response with correlation ID for traceability
- Ensure data integrity is maintained (no partial celebrations)
- Log error details with correlation ID

## Implementation Details

### Command Handler: AddAchievementCelebrationCommandHandler
- **Base Class**: extends CommonCommandHandler<AddAchievementCelebrationCommand>
- **Dependencies**: IAchievementRepository, IUserRepository, IAchievementCelebrationRepository, AchievementHandlerExceptionPolicy
- **Validation**: Inherits validateCommand() from CommonCommandHandler
- **Domain Events**: Publishes events via publishDomainEvents(achievement)
- **Response**: HandlerResponse.success(achievement.toKto())

### Command Model: AddAchievementCelebrationCommand
- **Base Class**: extends CommonCommand
- **JWT Extraction**: userKey automatically populated from JWT token
- **Path Parameter**: achievementKey from URL path parameter
- **Validation**: JSR-303 annotations for count field
- **Required Fields**: count (integer representing celebration intensity)

### Entity Creation: AchievementCelebration.create()
- **Factory Method**: AchievementCelebration.create(count, achievement, user)
- **Celebration Count**: Integer value representing celebration intensity/emoji count
- **User Association**: Links to the user who is celebrating
- **Achievement Association**: Links to the achievement being celebrated

### Repository Operations
- **User Lookup**: IUserRepository.findByKey(userKey, UserKto.class)
- **Achievement Retrieval**: IAchievementRepository.getAchievementByKey(Achievement.class, achievementKey)
- **Celebration Save**: IAchievementCelebrationRepository.save(celebration)

### Domain Events
- **Celebration Addition**: achievement.addCelebration(celebration) raises domain events
- **Event Publishing**: publishDomainEvents(achievement) publishes to message queue
- **Event Types**: AchievementCelebrationAddedEvent for downstream processing

### Error Handling: AchievementHandlerExceptionPolicy
- **Exception Mapping**: Maps domain exceptions to HTTP status codes
- **Correlation Tracking**: Includes correlation ID in all error responses
- **Structured Responses**: Consistent error format across all failures

## Data Validation Rules

### Required Fields (JSR-303)
- `userKey`: 7 characters exactly (from JWT token)
- `achievementKey`: 7 characters exactly (from URL path parameter)
- `count`: Integer value representing celebration intensity

### Business Rules
- **Multiple Celebrations**: Users can celebrate the same achievement multiple times
- **Self-Celebration**: Users can celebrate their own achievements
- **No Restrictions**: No limit on celebration count or frequency (business decision)

## Response Format

### Success Response (HTTP 200 OK)
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "responseType": "SUCCESS"
}
```

### Error Response Format
```json
{
  "success": false,
  "error": "Error message text",
  "correlationId": "uuid-correlation-id",
  "timestamp": "2025-09-26T10:30:00Z"
}
```

## HTTP Status Codes

### Success Codes
- **200 OK**: Celebration added successfully, returns achievement entity key

### Client Error Codes (4xx)
- **400 Bad Request**: JSR-303 validation failures (missing/invalid count)
- **401 Unauthorized**: Missing/invalid JWT token
- **404 Not Found**: User not found, achievement not found

### Server Error Codes (5xx)
- **500 Internal Server Error**: Database errors, message queue errors, unexpected system exceptions

## Message Queue Integration

### Event Publishing
- **Domain Events**: AchievementCelebrationAddedEvent published after successful save
- **Message Queue**: Events published to downstream systems for processing
- **Analytics**: Celebration events tracked for metrics and analytics
- **Notifications**: Potential notification triggers for achievement owners

### Event Data
- **Achievement Key**: Identifies the celebrated achievement
- **User Key**: Identifies the user who celebrated
- **Count**: Celebration intensity value
- **Timestamp**: When the celebration occurred

## Sample Test Data

### Valid Celebration Request
```json
{
  "count": 5
}
```

### Expected Success Response
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "responseType": "SUCCESS"
}
```

## Performance & Scalability

### Multiple Celebrations
- Efficient handling of multiple celebrations per achievement
- Domain events batched for optimal message queue performance
- Database optimization for celebration queries

### Event Processing
- Asynchronous event publishing to prevent blocking
- Message queue reliability for downstream processing
- Correlation ID tracking for distributed system debugging