package com.task.management.domain.project.port.in.command;

import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record UpdateTaskCommand(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotNull(message = "Task status is required")
        TaskStatus taskStatus,
        @NotNull
        ProjectUserId assigneeId
) {
    @Builder
    public UpdateTaskCommand {
    }
}
