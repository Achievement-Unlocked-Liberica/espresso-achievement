package espresso.common.domain.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


//@Data
//@MappedSuperclass
//@EqualsAndHashCode(callSuper = true)
public abstract class DomainAggregate extends DomainEntity {

    @Transient    
    protected final List<Object> domainEvents = new ArrayList<>();

    @DomainEvents
    public List<Object> getDomainEvents() {
        return domainEvents;
    }

    @AfterDomainEventPublication
    public void clearDomainEvents() {
        domainEvents.clear();
    }

}
