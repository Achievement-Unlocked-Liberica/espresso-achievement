package espresso.common.domain.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;

 

/**
 * Base class for domain aggregate roots following Domain-Driven Design (DDD) principles.
 * Extends DomainEntity to provide domain event management capabilities.
 * Aggregate roots are responsible for maintaining consistency boundaries and 
 * publishing domain events when important business operations occur.
 */
//@Data
//@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class DomainAggregate extends DomainEntity {

    /**
     * Collection of domain events that have been raised by this aggregate.
     * Events are accumulated and published after successful persistence operations.
     */
    @Transient 
    @JsonIgnore
    protected final List<Object> domainEvents = new ArrayList<>();

    /**
     * Returns the list of domain events accumulated by this aggregate.
     * Called by Spring Data to publish events after successful repository operations.
     * 
     * @return List of domain events to be published
     */
    @DomainEvents
    public List<Object> getDomainEvents() {
        return domainEvents;
    }

    /**
     * Clears the domain events collection after successful publication.
     * Called automatically by Spring Data after events have been published.
     */
    @AfterDomainEventPublication
    public void clearDomainEvents() {
        domainEvents.clear();
    }
}
