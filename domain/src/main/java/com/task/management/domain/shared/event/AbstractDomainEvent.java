package com.task.management.domain.shared.event;

import lombok.Data;

import java.time.Instant;

@Data
public abstract class AbstractDomainEvent implements DomainEvent {
    protected final Instant occurredAt;

    public AbstractDomainEvent() {
        this.occurredAt = Instant.now();
    }
}
