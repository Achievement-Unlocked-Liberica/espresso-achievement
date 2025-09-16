package espresso.common.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import espresso.common.domain.models.DomainAggregate;

public abstract class CommonCommandHandler {
    
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * Publishes domain events from the given aggregate.
     *
     * @param aggregate The aggregate containing domain events
     */
    protected void publishDomainEvents(DomainAggregate aggregate) {
        var domainEvents = aggregate.getDomainEvents();
        domainEvents.forEach(event -> applicationEventPublisher.publishEvent(event));
        aggregate.clearDomainEvents();
    }
}
