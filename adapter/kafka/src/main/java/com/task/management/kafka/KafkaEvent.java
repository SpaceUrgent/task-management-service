package com.task.management.kafka;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KafkaEvent {
    private String type;
    private String payload;

    @Builder
    public KafkaEvent(String type, String payload) {
        this.type = type;
        this.payload = payload;
    }
}
