package com.task.management.application.project.command;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

public record UpdateTaskCommand(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotNull(message = "Task status is required")
        String taskStatus,
        @NotNull(message = "Task priority is required")
        TaskPriority priority,
        UserId assigneeId,
        LocalDate dueDate
) {
    @Builder
    public UpdateTaskCommand {
    }
}
