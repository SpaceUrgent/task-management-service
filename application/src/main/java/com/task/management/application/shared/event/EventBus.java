package com.task.management.application.shared.event;

import com.task.management.application.shared.EventHandlingException;
import com.task.management.domain.shared.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.task.management.domain.shared.validation.Validation.eventRequired;

public class EventBus {
    private final List<DomainEventHandler<? extends DomainEvent>> eventHandlers;

    public EventBus() {
        this.eventHandlers = new ArrayList<>();
    }

    public void register(List<DomainEventHandler<? extends DomainEvent>> eventHandlers) {
        this.eventHandlers.addAll(eventHandlers);
    }

    public void dispatch(DomainEvent domainEvent) throws EventHandlingException {
         getHandlerForEvent(domainEvent).handle(domainEvent);
    }

    @SuppressWarnings("unchecked")
    private  <T extends DomainEvent> DomainEventHandler<T> getHandlerForEvent(T event) throws EventHandlingException {
        eventRequired(event);
        return (DomainEventHandler<T>) eventHandlers.stream()
                .filter(handler -> Objects.equals(event.getClass(), handler.eventType()))
                .findAny()
                .orElseThrow(() -> new EventHandlingException("Appropriate vent handler not found"));
    }
}
