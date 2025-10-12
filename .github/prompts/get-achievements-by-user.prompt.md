---
mode: agent
---
As a player, I want to see all the achievements that I have created, and see the short summary for each achievement

## Implementation Steps for 'getMyAchievements' Feature

### 1. Controller Layer
- **Class**: `AchievementController`
- **Method**: `getMyAchievements()`
- **Endpoint**: `GET /api/achievements/my`
- **Implementation**:
    - Extract user key from JWT token using `@AuthenticationPrincipal` or security context
    - Call service method `achievementService.getMyAchievements(userKey)`
    - Return `ResponseEntity<List<AchievementSummaryDto>>`

### 2. Handler Layer
- **Class**: `AchievementService`
- **Method**: `getMyAchievements(String userKey)`
- **Implementation**:
    - Validate user key parameter
    - Call repository method to fetch user's achievements
    - Map entities to DTOs using mapper
    - Return list of achievement summaries

### 3. Repository Layer
- **Class**: `AchievementRepository`
- **Method**: `findByCreatedByUserKey(String userKey)`
- **Implementation**:
    - Create JPA query method or custom query
    - Filter achievements by creator user key
    - Order by creation date (most recent first)

### 4. DTO Layer
- **Class**: `AchievementSummaryDto`
- **Fields**: `id`, `title`, `description`, `createdDate`, `status`
- **Purpose**: Lightweight representation for listing user's achievements

### 5. Security Integration
- **JWT Token Processing**: Extract user identity from authentication context
- **Authorization**: Ensure users can only see their own achievements