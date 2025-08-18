---
mode: agent
description: Update Achievement Endpoint: Implements an endpoint to update achievement details (title, description, skills, visibility) via PUT operation.

---

## User Story

As a player, I want to update the details of an achievement, so I can change its main information when needed.

## Description

Create an endpoint to update the title, description, skills, and visibility of an achievement using a PUT operation.

- Media updates are handled by separate endpoints.
- Removing media is a separate operation.

## Types

Define a new type: `UpdateAchievementCommand`.

- Fields and restrictions match `CreateAchievementCommand`.
- Add `achievementKey`:
    - Must not be blank.
    - Must be exactly 7 characters long.
- **Note**: Do not include `ALLOWED_SKILLS` constants in command types. These should reference centralized constants from `AchievementConstants`.

## Implementation Steps

### Constants

- Create `AchievementConstants` class in `espresso.achievement.domain.constants` package.
    - Define `ALLOWED_SKILLS` as a public static final Set.
    - Define `ERROR_INVALID_SKILL` as a public static final String.
    - Make class final with private constructor to prevent instantiation.

### Commands

- Update both `CreateAchievementCommand` and `UpdateAchievementCommand`:
    - Remove local `ALLOWED_SKILLS` and `ERROR_INVALID_SKILL` constants.
    - Import and reference `AchievementConstants` in validation methods.

### Achievement

- Add an `update` method.
    - Accepts title, description, skills, and visibility.
    - Updates respective properties in the domain class.

### AchievementCmdApi

- Add `updateAchievement` function.
    - Receives `UpdateAchievementCommand`.
    - Passes command to `AchievementCommandHandler`.

### AchievementCommandHandler

- Add a `handle` method for `UpdateAchievementCommand` (parameter: `cmd`).
    - Validate the command.
    - Retrieve user by `userKey`; throw not found error if missing.
    - Retrieve achievement by `achievementKey`; throw not found error if missing.
    - If found, call `update` on achievement with command properties.
    - Save updated achievement via `AchievementCmdRepository.update`.

### AchievementCmdRepository

- Add `update` method.
    - Receives `Achievement` instance.
    - Forwards to PSQL provider.

### AchievementPSQLProvider

- Add `update` method.
    - Updates achievement in database matching `id`, `achievementKey`, and `userKey`.

## Service Specs & Documentation

- Annotate service and command for Swagger UI visibility.
- Add code comments to methods describing their functionality.
- Add comments to internal logic explaining each step.

## API Testing

- Update `api-tests/achievement-restClient.http` with a new HTTP request:
    - Add PUT operation for updating achievements.
    - Use existing JWT token variable from the file.
    - Include sample `UpdateAchievementCommand` JSON payload.
    - Target endpoint: `PUT http://localhost:8080/api/cmd/achievement`.
    - Include proper headers (Content-Type, Authorization, X-API-Version).

## Testing

- Do not implement unit tests as part of this prompt.