package com.task.management.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.shared.EventHandlingException;
import com.task.management.application.shared.event.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class KafkaEventConsumerTest {

    @Mock
    private EventBus eventBus;
    private ObjectMapper objectMapper;
    private KafkaEventDeserializer deserializer;
    private KafkaEventConsumer consumer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        deserializer = new KafkaEventDeserializer(objectMapper);
        consumer = new KafkaEventConsumer(deserializer, eventBus);
    }

    @Test
    void consume() throws Exception {
        final var givenEvent = new SampleEvent(UUID.randomUUID().toString(), "Test");
        final var kafkaEvent = KafkaEvent.builder()
                .type(givenEvent.getClass().getCanonicalName())
                .payload(objectMapper.writeValueAsString(givenEvent))
                .build();
        final var jsonEvent = objectMapper.writeValueAsString(kafkaEvent);
        consumer.consume(jsonEvent);
        Mockito.verify(eventBus).dispatch(eq(givenEvent));
    }
}