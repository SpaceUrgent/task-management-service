package com.task.management.domain.project.port.in.command;

import com.task.management.domain.project.model.TaskId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record UpdateTaskCommand(
        @NotNull(message = "Task id is required")
        TaskId taskId,
        @NotBlank(message = "Title is required")
        String title,
        String description
) {
    @Builder
    public UpdateTaskCommand {
    }
}
