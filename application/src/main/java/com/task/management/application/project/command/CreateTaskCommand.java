package com.task.management.application.project.command;

import com.task.management.domain.common.model.objectvalue.UserId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;
import static com.task.management.domain.common.validation.Validation.presentOrFuture;

public record CreateTaskCommand(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotNull(message = "Assignee id is required")
        UserId assigneeId,
        LocalDate dueDate
) {
    @Builder
    public CreateTaskCommand {
        notBlank(title, "Title");
        parameterRequired(assigneeId, "Assignee id");
        Optional.ofNullable(dueDate).ifPresent(localDate -> presentOrFuture(localDate, "Due date"));
    }
}
