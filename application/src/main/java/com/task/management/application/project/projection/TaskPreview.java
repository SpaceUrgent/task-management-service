package com.task.management.application.project.projection;

import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.TaskNumber;
import com.task.management.domain.shared.model.objectvalue.TaskPriority;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public record TaskPreview(
        TaskId id,
        TaskNumber number,
        Instant createdAt,
        Instant updatedAt,
        LocalDate dueDate,
        String title,
        String status,
        TaskPriority priority,
        UserInfo assignee
) {
    @Builder
    public TaskPreview {
        parameterRequired(id, "Task id");
        parameterRequired(number, "Number");
        parameterRequired(createdAt, "Created time");
        notBlank(title, "Title");
        parameterRequired(status, "Status");
        parameterRequired(priority, "Priority");
    }
}
