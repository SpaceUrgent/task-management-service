package com.task.management.application.common.port.out;

import com.task.management.domain.common.event.DomainEvent;

import java.util.List;

public interface DomainEventPublisherPort {
    void publish(DomainEvent event);

    void publish(List<DomainEvent> events);
}
