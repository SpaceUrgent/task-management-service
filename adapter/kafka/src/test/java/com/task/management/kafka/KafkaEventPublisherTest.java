package com.task.management.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.domain.shared.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KafkaEventPublisherTest {
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;
    private static final String TOPIC = "test-events";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private KafkaEventPublisher publisher;

    @BeforeAll
    static void startKafka() {
        kafkaContainer.start();

        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", kafkaContainer.getBootstrapServers());
        producerProps.put("key.serializer", StringSerializer.class.getName());
        producerProps.put("value.serializer", StringSerializer.class.getName());
        producer = new KafkaProducer<>(producerProps);

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(List.of(TOPIC));
    }

    @AfterAll
    static void stopKafka() {
        if (producer != null) producer.close();
        if (consumer != null) consumer.close();
        kafkaContainer.stop();
    }

    @BeforeEach
    void setUp() {
        KafkaEventSerializer serializer = new KafkaEventSerializer(objectMapper);
        publisher = new KafkaEventPublisher(producer, serializer, TOPIC);
    }

    @Test
    void shouldPublishAndConsumeDomainEvent() throws JsonProcessingException {
        SampleEvent givenEvent = new SampleEvent(UUID.randomUUID().toString(), "TestPayload");
        publisher.publish(givenEvent);
        final var consumerRecords = consumer.poll(Duration.ofSeconds(2));
        assertEquals(1, consumerRecords.count());
        final var recordOptional = StreamSupport.stream(consumerRecords.records(TOPIC).spliterator(), false).findFirst();
        assertTrue(recordOptional.isPresent());
        final var recordPayload = recordOptional.get().value();
        final var kafkaEvent = objectMapper.readValue(recordPayload, KafkaEvent.class);
        final var eventPayload = kafkaEvent.getPayload();
        assertEquals(givenEvent.getClass().getCanonicalName(), kafkaEvent.getType());
        final var eventGot = objectMapper.readValue(eventPayload, givenEvent.getClass());
        assertEquals(givenEvent, eventGot);
    }
}