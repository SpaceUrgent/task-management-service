package com.task.management.domain.project.model;

import lombok.Builder;

import java.time.Instant;

import static com.task.management.domain.common.Validation.parameterRequired;

public record TaskPreview(
        TaskId id,
        Instant createdAt,
        String title,
        TaskStatus status,
        ProjectUser assignee
) {
    @Builder
    public TaskPreview {
        parameterRequired(id, "Task id");
        parameterRequired(createdAt, "Created time");
        parameterRequired(title, "Title");
        parameterRequired(status, "Status");
        parameterRequired(assignee, "Assignee");
    }
}
