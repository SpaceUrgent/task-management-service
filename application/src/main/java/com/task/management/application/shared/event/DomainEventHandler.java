package com.task.management.application.shared.event;

import com.task.management.application.shared.EventHandlingException;
import com.task.management.domain.shared.event.DomainEvent;

public interface DomainEventHandler<T extends DomainEvent> {

    void handle(T event) throws EventHandlingException;

    Class<T> eventType();
}
