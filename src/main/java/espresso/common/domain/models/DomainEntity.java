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
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@EqualsAndHashCode
public abstract class DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "entityKey", nullable = false)
    protected String entityKey;

    @Column(name = "registeredAt", nullable = false)
    @CreationTimestamp
    private OffsetDateTime registeredAt;

    @Column(name = "updatedAt", nullable = false)
    @CreationTimestamp
    private OffsetDateTime updatedAt;

    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default true")
    private boolean enabled = true;

    @Column(name = "active", nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    protected void updateEntity() {
        this.updatedAt = OffsetDateTime.now();
    }
}
