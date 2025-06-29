package com.task.management.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.task.management.application.shared.EventHandlingException;
import com.task.management.application.shared.event.EventBus;

public class KafkaEventConsumer {
    private final KafkaEventDeserializer eventDeserializer;
    private final EventBus eventBus;

    public KafkaEventConsumer(KafkaEventDeserializer eventDeserializer, EventBus eventBus) {
        this.eventDeserializer = eventDeserializer;
        this.eventBus = eventBus;
    }

    public void consume(String json) throws EventHandlingException {
        try {
            final var domainEvent = eventDeserializer.deserialize(json);
            eventBus.dispatch(domainEvent);
        } catch (JsonProcessingException | ClassNotFoundException e) {
            throw new EventHandlingException(e.getMessage());
        }
    }
}
