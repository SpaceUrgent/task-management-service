package com.task.management.application.shared;

import com.task.management.application.shared.event.DomainEventHandler;
import com.task.management.domain.shared.event.DomainEvent;

import java.util.List;
import java.util.Objects;

import static com.task.management.domain.shared.validation.Validation.eventRequired;

public class DomainEventHandlerFactory {

    private final List<DomainEventHandler<? extends DomainEvent>> eventHandlers;

    public DomainEventHandlerFactory(List<DomainEventHandler<? extends DomainEvent>> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> DomainEventHandler<T> supplyHandlerFor(T event) throws EventHandlingException {
        eventRequired(event);
        return (DomainEventHandler<T>) eventHandlers.stream()
                .filter(handler -> Objects.equals(event.getClass(), handler.eventType()))
                .findAny()
                .orElseThrow(() -> new EventHandlingException("Appropriate vent handler not found"));
    }
}
