package com.task.management.domain.project.projection;

import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskNumber;
import com.task.management.domain.project.model.TaskStatus;
import lombok.Builder;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record TaskPreview(
        TaskId id,
        TaskNumber number,
        Instant createdAt,
        Instant updatedAt,
        String title,
        TaskStatus status,
        MemberView assignee
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
