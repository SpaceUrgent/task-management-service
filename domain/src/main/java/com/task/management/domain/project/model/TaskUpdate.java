package com.task.management.domain.project.model;

import com.task.management.domain.common.model.UserId;
import lombok.Builder;

import java.time.LocalDate;

import static com.task.management.domain.common.validation.Validation.*;

public record TaskUpdate(
        String title,
        String description,
        TaskStatus status,
        LocalDate dueDate,
        UserId assignee
) {

    @Builder
    public TaskUpdate {
        notBlank(title, "Title");
        parameterRequired(status, "Status");
        parameterRequired(assignee, "Assignee");
    }
}
