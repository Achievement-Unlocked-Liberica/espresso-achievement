package espresso.common.domain.events;

/**
 * Enumeration of standard action types for domain events.
 * Provides a consistent vocabulary for describing what happened in domain events.
 * These action types help categorize events and enable generic event handling patterns.
 */
public enum EventActionTypes {
    /** Entity was successfully created */
    CREATED,
    /** Entity creation operation failed */
    CREATION_FAILED,
    /** Entity was successfully updated */
    UPDATED,
    /** Entity update operation failed */
    UPDATE_FAILED,
    /** Entity was successfully deleted */
    DELETED,
    /** Entity deletion operation failed */
    DELETE_FAILED,
    /** Entity was restored from deletion */
    RESTORED,
    /** Entity was archived for long-term storage */
    ARCHIVED,
    /** Entity was restored from archive */
    UNARCHIVED,
    /** Entity was published/made public */
    PUBLISHED,
    /** Entity was unpublished/made private */
    UNPUBLISHED,
    /** Entity was activated */
    ACTIVATED,
    /** Entity was deactivated */
    DEACTIVATED,
    /** Entity was enabled */
    ENABLED,
    /** Entity was disabled */
    DISABLED,
    /** Entity was locked for editing */
    LOCKED,
    /** Entity was unlocked for editing */
    UNLOCKED,
    /** Entity was suspended */
    SUSPENDED,
    /** Entity was resumed from suspension */
    RESUMED,
    /** Entity was assigned to someone/something */
    ASSIGNED,
    /** Entity assignment was removed */
    UNASSIGNED,
    /** Entity ownership was transferred */
    TRANSFERRED,
    /** Entity was moved to different location */
    MOVED,
    /** Entity was migrated to new system/format */
    MIGRATED,
    /** Import operation was started */
    IMPORT_STARTED,
    /** Import operation completed successfully */
    IMPORT_COMPLETED,
    /** Import operation failed */
    IMPORT_FAILED,
    /** Export operation was started */
    EXPORT_STARTED,
    /** Export operation completed successfully */
    EXPORT_COMPLETED,
    /** Export operation failed */
    EXPORT_FAILED,
    /** Entity was submitted for processing */
    SUBMITTED,
    /** Entity was approved */
    APPROVED,
    /** Entity was rejected */
    REJECTED,
    /** Issue was escalated to higher level */
    ESCALATED,
    /** Issue was de-escalated to lower level */
    DEESCALATED,
    /** Entities were merged together */
    MERGED,
    /** Entity was split into multiple entities */
    SPLIT,
    /** Entity was viewed by a user */
    VIEWED,
    /** Entity was accessed by a user */
    ACCESSED,
    /** Custom application-specific event */
    CUSTOM_EVENT
}
