package com.task.management.domain.common.event;

import com.task.management.domain.common.port.in.DomainEventHandlerPort;

import java.util.List;
import java.util.Objects;

import static com.task.management.domain.common.validation.Validation.eventRequired;

public class DomainEventHandlerFactory {

    private final List<DomainEventHandlerPort<? extends DomainEvent>> eventHandlers;

    public DomainEventHandlerFactory(List<DomainEventHandlerPort<? extends DomainEvent>> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> DomainEventHandlerPort<T> handle(T event) throws EventHandlingException {
        eventRequired(event);
        return (DomainEventHandlerPort<T>) eventHandlers.stream()
                .filter(handler -> Objects.equals(event.getClass(), handler.eventType()))
                .findAny()
                .orElseThrow(() -> new EventHandlingException("Appropriate vent handler not found"));
    }
}
