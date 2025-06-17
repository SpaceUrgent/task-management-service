package com.task.management.application.shared.port.in;

import com.task.management.application.shared.EventHandlingException;
import com.task.management.domain.shared.event.DomainEvent;

public interface DomainEventHandlerPort<T extends DomainEvent> {

    void handle(T event) throws EventHandlingException;

    Class<T> eventType();
}
