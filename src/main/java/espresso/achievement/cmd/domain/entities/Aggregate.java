package espresso.achievement.cmd.domain.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

public class Aggregate extends Entity {

    @Transient
    protected final List<Object> domainEvents = new ArrayList<>();

    @DomainEvents
    protected List<Object> getDomainEvents() {
        return domainEvents;
    }

    @AfterDomainEventPublication
    protected void clearDomainEvents() {
        domainEvents.clear();
    }

}
