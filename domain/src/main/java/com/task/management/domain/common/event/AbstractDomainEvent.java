package com.task.management.domain.common.event;

import com.task.management.domain.common.model.objectvalue.UserId;
import lombok.Data;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Data
public abstract class AbstractDomainEvent<EntityId> implements DomainEvent {
    protected final Instant occurredAt;
    protected final EntityId entityId;
    protected final UserId actorId;

    public AbstractDomainEvent(EntityId entityId, UserId actorId) {
        this.occurredAt = Instant.now();
        this.entityId = parameterRequired(entityId, "Entity id");
        this.actorId = parameterRequired(actorId, "Actor id");
    }
}
