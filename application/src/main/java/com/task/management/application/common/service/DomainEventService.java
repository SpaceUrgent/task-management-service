package com.task.management.application.common.service;

import com.task.management.application.common.DomainEventHandlerFactory;
import com.task.management.application.common.DomainEventPublishingException;
import com.task.management.application.common.EventHandlingException;
import com.task.management.application.common.port.out.DomainEventPublisherPort;
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
