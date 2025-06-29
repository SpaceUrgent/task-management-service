package com.task.management.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.domain.shared.event.DomainEvent;

public class KafkaEventSerializer  {
    private final ObjectMapper objectMapper;

    public KafkaEventSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serialize(DomainEvent domainEvent) throws JsonProcessingException {
        final var payload = objectMapper.writeValueAsString(domainEvent);
        final var type = domainEvent.getClass().getCanonicalName();
        final var kafkaEvent = new KafkaEvent(type, payload);
        return objectMapper.writeValueAsString(kafkaEvent);
    }
}
