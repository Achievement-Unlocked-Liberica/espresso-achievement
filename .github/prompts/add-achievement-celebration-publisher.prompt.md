---
mode: agent
---

## Story 3: Achievement Comment Feature

### Overview
As a user, I want to celebrate an achievement form another player, and I can show my support to their acomplishment

### Goal
this solution has two parts:
- Implement a publisher feature that receives the achievment celebration in the service, processes it and saves into RabbitMQ
- Implement a consumer feature that receives the achievement celebration from RabbitMQ, and on a schedule will updat ethe accumulated count on the achievement.

### Technical Requirements (Publisher)

#### API Endpoints
- Create a new service endpoint `addAchievementCelebration` as a POST endpoint at `/celebration`
- Endpoint should follow the same coding styles and patterns as existing Achievement service endpoints in `AchievementCmdApi`
- The endpoint should receive and process an `AddAchievementCelebrationCommand` object

#### Command Structure
- Create `AddAchievementCelebrationCommand` with:
    - achievementKey (required, 7 alphanumeric characters)
    - userKey (required, 7 alphanumeric characters)
    - count (required, integer greater than zero, less than 10)
- Implement appropriate validation for all fields with a `validate()` method

#### Command Handler
- Create a command handler for processing `AddAchievementCelebrationCommand`
- Add the `handle` method to the `AchivementCommandHandler` and its interface `IAchivementCommandHandler`
- Handler responsibilities:
  - Call the command`s validate method to perform validation
  - Verify dependent entities exist (achievement and user)
  - Create a new instance of the `AchievementCelebration` (see Domain Model section) domain model using the domain's `create` operation
  - Call the repository to save the new domain model instance
- Follow existing patterns for command handlers in the project

#### Domain Model
- Create `AchievementCelebration` inheriting from ValueEntity  with:
    - Required fields:
        - count (the number of celebrations given)
        - achievementKey (the key of the Achievmeent that recieves the celebration)
        - userKey (the key of the User that gave the celebration)
    - Auto-generated/system fields:
        - id (auto-incrementing long, primary key)
        - createdAt (UTC timestamp of creation)
        - updatedAt (UTC timestamp of last update)
        - status (enumeration: PENDING, APPROVED, FLAGGED, DELETED)
    - Implement a `create` operation for instantiating new celebrations

- Create the type `AchievementCelebrationAddedEvent` that extends `CommonEvent` from the `common` module
- Add the @Builder annotation to the this event class
- Add the following properties:
    - count
    - achievementKey
    - userKey    
 
- Extend `Achievement` with an `addCelebration` method, which receives an instance of `AchievementCelebration`

- The `addCelebration` method will have the following behavior:
    - Add the instance of `AchievementCelebration` to an internal list called `celebrations` (add a new property if needed)
    - Build a new `AchievementCelebrationAddedEvent` using the builder pattern, available form the @Builder annotaiton in the event class
    - Add the event to the inherited `domainEvents` list

- Do not implement relationship mapping annotations (value entities don't need the jpa relationship annotations)

#### Repository Layer
- Create repository class called `AchievementCelebrationRepository` in the `infrastrucutre/repositories` folder
- Create a new interface called `IAchievementCelebrationRepository` in the `domain/contracts` folder
- Add a new method to the repository class and interface called `save` and `emit`

- Create a PostgreSQL data provider called `AchievementCelebrationPSQLProvider` with a method called `save` that receives an `AchievementCelebration` instance
- The `save` method will not save the instance in the DB it will just return
- Make the `save` method @Transactional

- Create a RabbitMQ data provider called `AchievementCelebrationRMQProvider` with a method called `emit` that receives an `AchievementCelebration` instance
- The `emit` method will implement the functionality to publish the `AchievementCelebration` instance into a RabbotMQ queue
- The RabbitMQ queue name will be obtianed formt he `application.properties` from the `achievement.celebration.queue` (create the property if it does not exist)
- The value for `messaging.achievement.celebration.queue` will be set to `achievement.celebration.queue`

#### Error Handling
- Implement proper validation error responses
- Handle cases where achievement or user doesn`t exist
- Provide meaningful error messages

#### Documentation
- Add Javadoc comments to all methods
- Include brief inline comments explaining key functionality
- Document API endpoint for consumers

### Deliverables
- Complete implementation of the feature across all layers in the Achievement module folder
- A file named `add-achievement-celebration-publisher-ac.tx` containing acceptance criteria in BDD format for all scenarios
- Create the acceptance criteria file in a folder called `acceptanceCriteria` under the `achievement\services` folder

### Non-Requirements
- Test implementation (will be handled in a separate request)

### Coding Standards
- Follow existing project conventions and patterns
- All methods must have Javadoc comments
- Add brief inline comments explaining complex logic
- Maintain consistent naming conventions
