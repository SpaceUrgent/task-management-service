package com.task.managment.web.project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TaskChangeLogDto {
    private Instant occurredAt;
    private String logMessage;
    private String oldValue;
    private String newValue;
}
