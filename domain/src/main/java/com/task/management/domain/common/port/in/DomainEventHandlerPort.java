package com.task.management.domain.common.port.in;

import com.task.management.domain.common.event.DomainEvent;
import com.task.management.domain.common.event.EventHandlingException;

public interface DomainEventHandlerPort<T extends DomainEvent> {

    void handle(T event) throws EventHandlingException;

    Class<T> eventType();
}
