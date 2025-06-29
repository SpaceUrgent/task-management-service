package com.task.management.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.domain.shared.event.DomainEvent;

public class KafkaEventDeserializer {
    private final ObjectMapper objectMapper;

    public KafkaEventDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DomainEvent deserialize(String payload) throws JsonProcessingException, ClassNotFoundException {
        final var kafkaEvent = objectMapper.readValue(payload, KafkaEvent.class);
        final var type = kafkaEvent.getType();
        final var eventClass = (Class<? extends DomainEvent>) Class.forName(type);
        return objectMapper.readValue(kafkaEvent.getPayload(), eventClass);
    }
}
