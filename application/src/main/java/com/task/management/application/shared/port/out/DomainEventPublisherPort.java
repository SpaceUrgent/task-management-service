package com.task.management.application.shared.port.out;

import com.task.management.domain.shared.event.DomainEvent;

import java.util.List;

public interface DomainEventPublisherPort {
    void publish(DomainEvent event);

    default void publish(List<DomainEvent> events) {
        if (events != null) {
            events.forEach(this::publish);
        }
    }
}
