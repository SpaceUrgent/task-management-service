package com.task.management.domain.project.model;

import lombok.Builder;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record TaskPreview(
        TaskId id,
        TaskNumber number,
        Instant createdAt,
        String title,
        TaskStatus status,
        ProjectUser assignee
) {
    @Builder
    public TaskPreview {
        parameterRequired(id, "Task id");
        parameterRequired(number, "Number");
        parameterRequired(createdAt, "Created time");
        notBlank(title, "Title");
        parameterRequired(status, "Status");
        parameterRequired(assignee, "Assignee");
    }
}
