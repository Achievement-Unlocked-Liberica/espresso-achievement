# Achievement DTO Hierarchy Documentation

This document describes the Data Transfer Object (DTO) hierarchy for Achievement and AchievementMedia entities.

## DTO Size Philosophy

DTOs are organized by size to provide different levels of detail:
- **SM (Small)**: Minimal essential data for listing/previews
- **MD (Medium)**: Standard data for most operations
- **LG (Large)**: Detailed data including related entities
- **XL (Extra Large)**: Complete data with all relationships and metadata

## Achievement DTOs

### AchievementDtoSm
**Purpose**: Minimal achievement information for lists and previews
**Properties**:
- entityKey
- title
- description

### AchievementDtoMd
**Purpose**: Standard achievement information with user and metadata
**Properties**:
- entityKey
- title
- description
- completedDate
- user (UserDtoSm)
- skills
- achievementVisibility

### AchievementDtoLg
**Purpose**: Detailed achievement with basic media information
**Properties**:
- entityKey
- title
- description
- completedDate
- user (UserDtoMd)
- skills
- achievementVisibility
- media (List<AchievementMediaDtoMd>)

### AchievementDtoXl
**Purpose**: Complete achievement information with full media details
**Properties**:
- entityKey
- title
- description
- completedDate
- user (UserDtoLg)
- skills
- achievementVisibility
- media (List<AchievementMediaDtoLg>)

## AchievementMedia DTOs

### AchievementMediaDtoSm
**Purpose**: Minimal media information for thumbnails/previews
**Properties**:
- imageKey
- imageName
- contentType

### AchievementMediaDtoMd
**Purpose**: Standard media information with metadata
**Properties**:
- imageKey
- imageName
- originalImageName
- contentType
- mediaUrl
- uploadTimestamp
- fileSize

### AchievementMediaDtoLg
**Purpose**: Detailed media with achievement reference
**Properties**:
- imageKey
- imageName
- originalImageName
- contentType
- mediaUrl
- uploadTimestamp
- fileSize
- achievement (Achievement entity reference)

### AchievementMediaDtoXl
**Purpose**: Complete media information including binary data
**Properties**:
- imageKey
- imageName
- originalImageName
- contentType
- mediaUrl
- uploadTimestamp
- fileSize
- achievement (Achievement entity reference)
- imageData (binary data)

## Usage Guidelines

1. **Use SM DTOs** for list endpoints where only basic info is needed
2. **Use MD DTOs** for standard API responses and forms
3. **Use LG DTOs** when related entity details are required
4. **Use XL DTOs** sparingly, only when complete data is absolutely necessary

## Relationship Mapping

- Achievement LG/XL → includes User DTOs (reusing existing user module DTOs)
- AchievementMedia LG/XL → includes Achievement entity reference
- Circular dependency avoided by using entity references instead of DTO references where needed
