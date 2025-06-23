package com.task.management.domain.shared.event;

import com.task.management.domain.shared.model.objectvalue.UserId;
import lombok.Data;

import java.time.Instant;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Data
public abstract class AbstractDomainEvent<EntityId> implements DomainEvent {
    protected final Instant occurredAt;
    protected final EntityId entityId;

    public AbstractDomainEvent(EntityId entityId) {
        this.occurredAt = Instant.now();
        this.entityId = parameterRequired(entityId, "Entity id");
    }
}
