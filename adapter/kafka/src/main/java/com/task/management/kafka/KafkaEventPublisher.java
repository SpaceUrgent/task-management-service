package com.task.management.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.task.management.application.shared.DomainEventPublishingException;
import com.task.management.application.shared.port.out.DomainEventPublisherPort;
import com.task.management.domain.shared.event.DomainEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import static com.task.management.domain.shared.validation.Validation.eventRequired;

public class KafkaEventPublisher implements DomainEventPublisherPort {
    private final KafkaProducer<String, String> kafkaProducer;
    private final KafkaEventSerializer kafkaEventSerializer;
    private final String domainEventTopic;

    public KafkaEventPublisher(KafkaProducer<String, String> kafkaProducer,
                               KafkaEventSerializer kafkaEventSerializer,
                               String domainEventTopic) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaEventSerializer = kafkaEventSerializer;
        this.domainEventTopic = domainEventTopic;
    }

    @Override
    public void publish(DomainEvent event) {
        eventRequired(event);
        try {
            final var serializedEvent = kafkaEventSerializer.serialize(event);
            kafkaProducer.send(new ProducerRecord<>(domainEventTopic, serializedEvent));
        } catch (JsonProcessingException e) {
            throw new DomainEventPublishingException(e.getMessage(), e);
        }
    }
}
