package com.task.management.domain.shared.model;

import com.task.management.domain.shared.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public abstract class DomainEventAggregate {
    private final transient List<DomainEvent> events = new ArrayList<>();

    public List<DomainEvent> flushEvents() {
        final var events = new ArrayList<>(this.events);
        this.events.clear();
        return events;
    }

    protected void add(DomainEvent event) {
        parameterRequired(event, "Event");
        this.events.add(event);
    }
}
