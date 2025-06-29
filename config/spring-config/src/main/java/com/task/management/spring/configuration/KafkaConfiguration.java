package com.task.management.spring.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.shared.event.EventBus;
import com.task.management.application.shared.port.out.DomainEventPublisherPort;
import com.task.management.kafka.KafkaEventConsumer;
import com.task.management.kafka.KafkaEventDeserializer;
import com.task.management.kafka.KafkaEventPublisher;
import com.task.management.kafka.KafkaEventSerializer;
import com.task.management.spring.listener.KafkaEventListener;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfiguration {

    @Value("${kafka.topics.domain-event}")
    private String domainEventTopic;

    @Bean
    public KafkaEventListener kafkaEventListener(KafkaEventConsumer consumer) {
        return new KafkaEventListener(consumer);
    }

    @Bean
    public DomainEventPublisherPort domainEventPublisherPort(KafkaProducer<String, String> kafkaProducer,
                                                             KafkaEventSerializer kafkaEventSerializer) {
        return new KafkaEventPublisher(kafkaProducer, kafkaEventSerializer, domainEventTopic);
    }

    @Bean
    public KafkaEventConsumer kafkaEventConsumer(EventBus eventBus, KafkaEventDeserializer kafkaEventDeserializer) {
        return new KafkaEventConsumer(kafkaEventDeserializer, eventBus);
    }

    @Bean
    public KafkaEventSerializer kafkaEventSerializer(ObjectMapper objectMapper) {
        return new KafkaEventSerializer(objectMapper);
    }

    @Bean
    public KafkaEventDeserializer kafkaEventDeserializer(ObjectMapper objectMapper) {
        return new KafkaEventDeserializer(objectMapper);
    }

    @Bean
    public KafkaProducer<String, String> kafkaProducer(KafkaProperties kafkaProperties) {
        final var properties = kafkaProperties.buildProducerProperties();
        return new KafkaProducer<>(properties);
    }

}
