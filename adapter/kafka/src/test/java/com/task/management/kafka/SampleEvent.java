package com.task.management.kafka;

import com.task.management.domain.shared.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleEvent implements DomainEvent {
    private String id;
    private String text;
}
