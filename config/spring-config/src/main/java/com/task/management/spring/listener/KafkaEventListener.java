package com.task.management.spring.listener;

import com.task.management.application.shared.EventHandlingException;
import com.task.management.kafka.KafkaEventConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

public class KafkaEventListener {
    private final KafkaEventConsumer kafkaEventConsumer;

    public KafkaEventListener(KafkaEventConsumer kafkaEventConsumer) {
        this.kafkaEventConsumer = kafkaEventConsumer;
    }

    @Transactional
    @KafkaListener(
            topics = "${kafka.topics.domain-event}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(String message) throws EventHandlingException {
        kafkaEventConsumer.consume(message);
    }
}
