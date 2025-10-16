# Upload Challenge Media - Acceptance Criteria

## Feature: Upload Challenge Media Endpoint

### User Story
As a player, I want to upload media files to my challenges, so that I can share visual content and enhance my challenge documentation.

### Endpoint Details
- **Method**: POST
- **URL**: `/api/cmd/challenge/{challengeKey}/media`
- **Authentication**: JWT token required (userKey extracted automatically)
- **Content-Type**: multipart/form-data
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Media Upload
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists in the system with key "ABC1234"
**And** a challenge exists with key "8NctRKY" owned by user "ABC1234"
**And** the request contains valid image files in multipart form:
```
POST /api/cmd/challenge/8NctRKY/media
Content-Type: multipart/form-data
Authorization: Bearer <jwt_token>

--boundary
Content-Disposition: form-data; name="images"; filename="screenshot.png"
Content-Type: image/png

[binary image data]
--boundary--
```
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Extract userKey from JWT token (not from request body)
- Extract challengeKey "8NctRKY" from URL path parameter
- Validate command via CommonCommand.validateCommand()
- Look up user by key "ABC1234" via IUserRepository.findByKey()
- Retrieve challenge by key "8NctRKY" via IChallengeRepository.getChallengeByKey()
- Verify user "ABC1234" owns the challenge via challenge.isCreator()
- Process each image in cmd.getImages() array
- Create ChallengeMedia entity for each image via ChallengeMedia.create()
- Save media via IChallengeMediaRepository.save(challenge, media)
- Add media to challenge via challenge.addMedia(savedMedia) (raises domain events)
- Publish domain events via publishDomainEvents(challenge)
- Return HTTP 200 OK
- Return response with entity key:
```json
{
  "success": true,
  "data": {
    "id": 123,
    "entityKey": "8NctRKY"
  },
  "responseType": "SUCCESS"
}
```

### AC2: Authentication Failures

#### AC2.1: Missing JWT Token
**Given** no JWT token is provided in the request
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no media uploaded)

#### AC2.2: Invalid or Expired JWT Token
**Given** an invalid or expired JWT token is provided
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no media uploaded)

### AC3: User Not Found
**Given** a valid JWT token with userKey "XYZ9999"
**And** no user exists in the system with key "XYZ9999"
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
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

### AC4: Challenge Not Found
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** no challenge exists with key "INVALID"
**When** the POST request is made to `/api/cmd/challenge/INVALID/media`
**Then** the system should:
- Extract userKey from JWT token
- Look up user successfully
- Attempt challenge lookup via IChallengeRepository.getChallengeByKey("INVALID")
- Find challenge is null
- Return HandlerResponse.error("Challenge not found", ResponseType.NOT_FOUND)
- Return HTTP 404 Not Found
- Return error response with correlation ID:
```json
{
  "success": false,
  "error": "Challenge not found",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no media uploaded)

### AC5: Unauthorized Access (Not Owner)
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** a challenge exists with key "8NctRKY" owned by user "OTHER123"
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Extract userKey "ABC1234" from JWT token
- Look up user "ABC1234" successfully
- Retrieve challenge "8NctRKY" successfully
- Check challenge.isCreator(User.fromKto(userKto)) returns false
- Return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO UPLOAD MEDIA FOR THIS CHALLENGE", ResponseType.UNAUTHORIZED)
- Return HTTP 401 Unauthorized
- Return error response with correlation ID:
```json
{
  "success": false,
  "error": "LOCALIZE: USER IS NOT AUTHORIZED TO UPLOAD MEDIA FOR THIS CHALLENGE",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no media uploaded)

### AC6: File Validation Failures

#### AC6.1: Empty Image Upload
**Given** valid JWT token and challenge ownership
**And** the request contains no image files
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Validate via JSR-303 annotations on UploadChallengeMediaCommand
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: CHALLENGE MEDIA CANNOT BE EMPTY"
- Take no action (no media uploaded)

#### AC6.2: File Size Validation
**Given** valid JWT token and challenge ownership
**And** the request contains files exceeding maximum size limit (10MB)
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Validate file size limits
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: CHALLENGE MEDIA EXCEEDS MAXIMUM SIZE OF 10MB"
- Take no action (no media uploaded)

#### AC6.3: Invalid Content Type
**Given** valid JWT token and challenge ownership
**And** the request contains files with invalid MIME types (not image/jpeg, image/png, image/gif, image/webp)
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Validate content types against ALLOWED_CONTENT_TYPES list
- Return HTTP 400 Bad Request
- Return validation error for invalid content type
- Take no action (no media uploaded)

#### AC6.4: Image Dimensions Too Small
**Given** valid JWT token and challenge ownership
**And** the request contains images with dimensions less than 200x200 pixels
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Validate image dimensions using BufferedImage
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: IMAGE DIMENSIONS TOO SMALL. MINIMUM SIZE IS 200X200 PIXELS"
- Take no action (no media uploaded)

#### AC6.5: Image Dimensions Too Large
**Given** valid JWT token and challenge ownership
**And** the request contains images with dimensions greater than 3000x3000 pixels
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Validate image dimensions using BufferedImage
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: IMAGE DIMENSIONS TOO LARGE. MAXIMUM SIZE IS 3000X3000 PIXELS"
- Take no action (no media uploaded)

#### AC6.6: Invalid Filename Characters
**Given** valid JWT token and challenge ownership
**And** the request contains images with invalid filename characters
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Validate filename against FILENAME_REGEX_PATTERN
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: INVALID FILENAME. ONLY ALPHANUMERIC CHARACTERS, DOTS, HYPHENS, AND UNDERSCORES ARE ALLOWED"
- Take no action (no media uploaded)

### AC7: Multiple File Processing
**Given** valid JWT token and challenge ownership
**And** the request contains multiple valid image files
**When** the POST request is made to `/api/cmd/challenge/8NctRKY/media`
**Then** the system should:
- Process each image in cmd.getImages() array using for loop
- Create ChallengeMedia entity for each image via ChallengeMedia.create()
- Include original filename via image.getOriginalFilename()
- Include content type via image.getContentType()
- Include binary data via image.getBytes()
- Save each media via IChallengeMediaRepository.save(challenge, media)
- Add each saved media to challenge via challenge.addMedia(savedMedia)
- Publish all domain events once via publishDomainEvents(challenge)
- Return success response with challenge entity key

### AC8: Media Storage and URL Generation
**Given** valid JWT token, challenge ownership, and valid image files
**When** media is saved via IChallengeMediaRepository.save()
**Then** the system should:
- Construct directory path: mediaDirectory + "/" + user.getEntityKey()
- Upload image to S3 via s3DataProvider.uploadImage(directory, challengeMedia)
- Generate unique image key: challengeKey + "-" + random7CharKey
- Store original filename and generated filename
- Clear imageData from entity before response (set to null)
- Set mediaUrl with S3 object storage path
- Persist metadata to PostgreSQL via psqlProvider.save()
- Return saved ChallengeMedia with mediaUrl populated

### AC9: Domain Event Publication
**Given** successful media upload
**When** challenge.addMedia(savedMedia) is called
**Then** the system should:
- Add media to challenge's media list (initialize list if null)
- Update entity timestamp via updateEntity()
- Raise ChallengeMediaEvent via raiseMediaAdded(media)
- Include event details: CREATED event type, challengeKey, userKey, imageKey, mediaUrl, originalImageName, contentType, fileSize
- Set event source to "challenge-module"
- Generate unique eventId (7-character key)
- Set timestamp to current OffsetDateTime
- Add event to challenge.domainEvents collection
- Publish events via publishDomainEvents(challenge) in handler

### AC10: Exception Handling
**Given** any unexpected error during media upload
**When** an exception occurs
**Then** the system should:
- Catch exception in handler's try-catch block
- Handle via ChallengeHandlerExceptionPolicy.handleException(ex, "upload media to challenge")
- Return appropriate HTTP error code (500 for unexpected errors)
- Include correlation ID in error response
- Log error details for troubleshooting
- Take no partial action (rollback if needed)

### AC11: Validation Order
**Given** any upload request
**When** the handler processes the command
**Then** validation should occur in this order:
1. JSR-303 validation via validateCommand(cmd)
2. Custom validation via cmd.validateCustom()
3. User existence check
4. Challenge existence check
5. Authorization check (isCreator)
6. File-level validation (size, type, dimensions, filename)
7. Content safety validation (if enabled)
**And** return first validation failure encountered
**And** proceed to processing only if all validations pass
