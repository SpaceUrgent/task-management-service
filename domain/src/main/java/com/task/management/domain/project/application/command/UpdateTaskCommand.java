package com.task.management.domain.project.application.command;

import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

public record UpdateTaskCommand(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotNull(message = "Task status is required")
        TaskStatus taskStatus,
        @NotNull
        UserId assigneeId,
        LocalDate dueDate
) {
    @Builder
    public UpdateTaskCommand {
    }
}
