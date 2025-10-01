# Upload Achievement Media - Acceptance Criteria

## Feature: Upload Achievement Media Endpoint

### User Story
As a player, I want to upload media files to my achievements, so that I can share visual content and enhance my achievement documentation.

### Endpoint Details
- **Method**: POST
- **URL**: `/api/cmd/achievement/{achievementKey}/media`
- **Authentication**: JWT token required (userKey extracted automatically)
- **Content-Type**: multipart/form-data
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Media Upload
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists in the system with key "ABC1234"
**And** an achievement exists with key "8NctRKY" owned by user "ABC1234"
**And** the request contains valid image files in multipart form:
```
POST /api/cmd/achievement/8NctRKY/media
Content-Type: multipart/form-data
Authorization: Bearer <jwt_token>

--boundary
Content-Disposition: form-data; name="images"; filename="screenshot.png"
Content-Type: image/png

[binary image data]
--boundary--
```
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
**Then** the system should:
- Extract userKey from JWT token (not from request body)
- Extract achievementKey "8NctRKY" from URL path parameter
- Validate command via CommonCommand.validateCommand()
- Look up user by key "ABC1234" via IUserRepository.findByKey()
- Retrieve achievement by key "8NctRKY" via IAchievementRepository.getAchievementByKey()
- Verify user "ABC1234" owns the achievement via achievement.isCreator()
- Process each image in cmd.getImages() array
- Create AchievementMedia entity for each image via AchievementMedia.create()
- Save media via IAchievementMediaRepository.save(achievement, media)
- Add media to achievement via achievement.addMedia(savedMedia) (raises domain events)
- Publish domain events via publishDomainEvents(achievement)
- Return HTTP 200 OK
- Return response with entity key:
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
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no media uploaded)

#### AC2.2: Invalid or Expired JWT Token
**Given** an invalid or expired JWT token is provided
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no media uploaded)

### AC3: User Not Found
**Given** a valid JWT token with userKey "XYZ9999"
**And** no user exists in the system with key "XYZ9999"
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
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
- Take no action (no media uploaded)

### AC4: Achievement Not Found
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** no achievement exists with key "INVALID"
**When** the POST request is made to `/api/cmd/achievement/INVALID/media`
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
- Take no action (no media uploaded)

### AC5: Unauthorized Access (Not Owner)
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** an achievement exists with key "8NctRKY" owned by user "OTHER123"
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
**Then** the system should:
- Extract userKey "ABC1234" from JWT token
- Look up user "ABC1234" successfully
- Retrieve achievement "8NctRKY" successfully
- Check achievement.isCreator(User.fromKto(userKto)) returns false
- Return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT", ResponseType.UNAUTHORIZED)
- Return HTTP 401 Unauthorized
- Return error response with correlation ID:
```json
{
  "success": false,
  "error": "LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no media uploaded)

### AC6: File Validation Failures

#### AC6.1: Empty Image Upload
**Given** valid JWT token and achievement ownership
**And** the request contains no image files
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
**Then** the system should:
- Validate via JSR-303 annotations on UploadAchievementMediaCommand
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: ACHIEVEMENT MEDIA CANNOT BE EMPTY"
- Take no action (no media uploaded)

#### AC6.2: File Size Validation
**Given** valid JWT token and achievement ownership
**And** the request contains files exceeding maximum size limit (10MB)
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
**Then** the system should:
- Validate file size limits
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: ACHIEVEMENT MEDIA EXCEEDS MAXIMUM SIZE OF 10MB"
- Take no action (no media uploaded)

#### AC6.3: Invalid Content Type
**Given** valid JWT token and achievement ownership
**And** the request contains files with invalid MIME types (not image/jpeg, image/png, image/gif, image/webp)
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
**Then** the system should:
- Validate content types against ALLOWED_CONTENT_TYPES list
- Return HTTP 400 Bad Request
- Return validation error for invalid content type
- Take no action (no media uploaded)

### AC7: Multiple File Processing
**Given** valid JWT token and achievement ownership
**And** the request contains multiple valid image files
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
**Then** the system should:
- Process each image in cmd.getImages() array using for loop
- Create AchievementMedia entity for each image via AchievementMedia.create()
- Include original filename via image.getOriginalFilename()
- Include content type via image.getContentType()
- Include binary data via image.getBytes()
- Save each media via IAchievementMediaRepository.save(achievement, media)
- Add each saved media to achievement via achievement.addMedia(savedMedia)
- Publish all domain events once via publishDomainEvents(achievement)
- Return success response with achievement entity key

### AC8: Content Safety Validation (Future Feature)
**Given** valid JWT token, achievement ownership, and valid image files
**And** content safety validation is enabled
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
**Then** the system should:
- Extract image content via image.getBytes()
- Validate content safety via IContentSafetyAIService.verifyImageContent()
- Reject inappropriate content with appropriate error message
- Allow safe content to proceed with normal processing
**Note**: Currently commented out in implementation

### AC9: Database and System Errors
**Given** a valid upload request that should succeed
**And** a database error occurs during the media save process
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/media`
**Then** the system should:
- Catch exception in AchievementHandlerExceptionPolicy.handleException()
- Return HTTP 500 Internal Server Error
- Return error response with correlation ID for traceability
- Ensure data integrity is maintained (no partial media saves)
- Log error details with correlation ID

## Implementation Details

### Command Handler: UploadAchievementMediaCommandHandler
- **Base Class**: extends CommonCommandHandler<UploadAchievementMediaCommand>
- **Dependencies**: IAchievementRepository, IAchievementMediaRepository, IUserRepository, IContentSafetyAIService, AchievementHandlerExceptionPolicy
- **Validation**: Inherits validateCommand() from CommonCommandHandler
- **Domain Events**: Publishes events via publishDomainEvents(achievement)
- **Response**: HandlerResponse.success(achievement.toKto())

### Command Model: UploadAchievementMediaCommand
- **Base Class**: extends CommonCommand
- **JWT Extraction**: userKey automatically populated from JWT token
- **Path Parameter**: achievementKey from URL path parameter
- **File Handling**: images[] array of MultipartFile objects
- **Validation**: File size, content type, and non-empty validation

### Entity Creation: AchievementMedia.create()
- **Factory Method**: AchievementMedia.create(achievement, filename, contentType, bytes)
- **File Metadata**: Stores original filename and MIME content type
- **Binary Storage**: Stores file content as byte array
- **Achievement Association**: Links to parent achievement entity

### Repository Operations
- **User Lookup**: IUserRepository.findByKey(userKey, UserKto.class)
- **Achievement Retrieval**: IAchievementRepository.getAchievementByKey(Achievement.class, achievementKey)
- **Media Save**: IAchievementMediaRepository.save(achievement, media)
- **Authorization Check**: achievement.isCreator(User.fromKto(userKto))

### Domain Events
- **Media Addition**: achievement.addMedia(savedMedia) raises domain events
- **Event Publishing**: publishDomainEvents(achievement) at end of processing
- **Event Types**: AchievementMediaEvent for media addition tracking

### Error Handling: AchievementHandlerExceptionPolicy
- **Exception Mapping**: Maps domain exceptions to HTTP status codes
- **Correlation Tracking**: Includes correlation ID in all error responses
- **Structured Responses**: Consistent error format across all failures

## Data Validation Rules

### Required Fields (JSR-303)
- `userKey`: 7 characters exactly (from JWT token)
- `achievementKey`: 7 characters exactly (from URL path parameter)
- `images`: Non-empty array of MultipartFile objects

### File Validation
- **Content Types**: Must be from ALLOWED_CONTENT_TYPES: ["image/jpeg", "image/png", "image/gif", "image/webp"]
- **File Size**: Maximum 10MB per file
- **File Count**: At least 1 file required

### Optional Features
- **Content Safety**: AI-based image content validation (currently disabled)

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
- **200 OK**: Media uploaded successfully, returns achievement entity key

### Client Error Codes (4xx)
- **400 Bad Request**: File validation failures (empty files, size limits, invalid types)
- **401 Unauthorized**: Missing/invalid JWT token, user not authorized to upload media
- **404 Not Found**: User not found, achievement not found

### Server Error Codes (5xx)
- **500 Internal Server Error**: Database errors, file processing errors, unexpected system exceptions

## Security & Performance

### Authentication & Authorization
- JWT token required with automatic userKey extraction
- User must own the achievement (achievement.isCreator() check)
- No cross-user media uploads allowed

### File Security
- Content type validation against allowed MIME types
- File size limits to prevent abuse
- Future: AI-based content safety validation
- Binary data stored securely in database

### Performance Considerations
- Multiple file processing in single request
- Domain events published once after all files processed
- Efficient binary storage with metadata indexing
- Transaction management for atomicity

## Sample Test Data

### Valid Upload Request
```
POST /api/cmd/achievement/8NctRKY/media
Content-Type: multipart/form-data
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

--boundary
Content-Disposition: form-data; name="images"; filename="achievement_screenshot.png"
Content-Type: image/png

[PNG binary data]
--boundary
Content-Disposition: form-data; name="images"; filename="progress_chart.jpg"  
Content-Type: image/jpeg

[JPEG binary data]
--boundary--
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