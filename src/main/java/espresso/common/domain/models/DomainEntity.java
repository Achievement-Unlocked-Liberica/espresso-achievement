package espresso.common.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.time.OffsetDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all domain entities in the system.
 * Provides common properties and behavior that all domain entities share,
 * including unique identification, timestamps, and lifecycle management.
 * Implements a consistent entity pattern across all domain aggregates.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EqualsAndHashCode
public abstract class DomainEntity {

    /**
     * The database primary key identifier for the entity.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * The 7-character alphanumeric unique key for the entity.
     * Used for external references and API operations.
     */
    @Column(name = "entityKey", nullable = false)
    protected String entityKey;

    /**
     * Timestamp when the entity was first created in the system (UTC).
     * Automatically set by Hibernate on entity creation.
     */
    @Column(name = "registeredAt", nullable = false)
    @CreationTimestamp
    private OffsetDateTime registeredAt;

    /**
     * Timestamp when the entity was last modified (UTC).
     * Automatically updated when entity is persisted.
     */
    @Column(name = "updatedAt", nullable = false)
    @CreationTimestamp
    private OffsetDateTime updatedAt;

    /**
     * Indicates whether the entity is enabled and should appear in queries.
     * When false, entity is excluded from normal operations without deletion.
     */
    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default true")
    @Default
    private boolean enabled = true;

    /**
     * Indicates whether the entity is active in the system.
     * Used for soft deletion and lifecycle management.
     */
    @Column(name = "active", nullable = false, columnDefinition = "boolean default true")
    @Default
    private boolean active = true;

    /**
     * Updates the entity's modification timestamp to the current time.
     * Should be called whenever the entity is modified.
     */
    protected void updateEntity() {
        this.updatedAt = OffsetDateTime.now();
    }
}
