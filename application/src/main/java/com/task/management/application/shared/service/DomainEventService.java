package com.task.management.application.shared.service;

import com.task.management.application.shared.DomainEventHandlerFactory;
import com.task.management.application.shared.DomainEventPublishingException;
import com.task.management.application.shared.EventHandlingException;
import com.task.management.application.shared.port.out.DomainEventPublisherPort;
import com.task.management.domain.shared.event.DomainEvent;

import java.util.List;

public class DomainEventService implements DomainEventPublisherPort {
    private final DomainEventHandlerFactory domainEventHandlerFactory;

    public DomainEventService(DomainEventHandlerFactory domainEventHandlerFactory) {
        this.domainEventHandlerFactory = domainEventHandlerFactory;
    }

    @Override
    public void publish(DomainEvent event) {
        try {
            final var eventHandler = domainEventHandlerFactory.supplyHandlerFor(event);
            eventHandler.handle(event);
        } catch (EventHandlingException e) {
            throw new DomainEventPublishingException("Failed publish event", e);
        }
    }

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}
