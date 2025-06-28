package com.task.management.application.shared.event;

import com.task.management.application.shared.DomainEventPublishingException;
import com.task.management.application.shared.EventHandlingException;
import com.task.management.application.shared.port.out.DomainEventPublisherPort;
import com.task.management.domain.shared.event.DomainEvent;

public class SimpleDomainEventPublisher extends EventBus implements DomainEventPublisherPort {

    @Override
    public void publish(DomainEvent event) {
        try {
           this.dispatch(event);
        } catch (EventHandlingException e) {
            throw new DomainEventPublishingException("Failed to publish event", e);
        }
    }
}
