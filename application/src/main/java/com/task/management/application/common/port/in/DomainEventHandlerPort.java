package com.task.management.application.common.port.in;

import com.task.management.application.common.EventHandlingException;
import com.task.management.domain.common.event.DomainEvent;

public interface DomainEventHandlerPort<T extends DomainEvent> {

    void handle(T event) throws EventHandlingException;

    Class<T> eventType();
}
