package com.task.management.application.project.model;

import lombok.Builder;

import java.time.Instant;

import static com.task.management.application.common.Validation.parameterRequired;

public record TaskPreview(
        TaskId id,
        Instant createdTime,
        String title,
        TaskStatus status,
        ProjectUser assignee
) {
    @Builder
    public TaskPreview {
        parameterRequired(id, "Task id");
        parameterRequired(createdTime, "Created time");
        parameterRequired(title, "Title");
        parameterRequired(status, "Status");
        parameterRequired(assignee, "Assignee");
    }
}
