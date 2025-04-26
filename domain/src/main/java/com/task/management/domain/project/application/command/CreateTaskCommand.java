package com.task.management.domain.project.application.command;

import com.task.management.domain.common.model.UserId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record CreateTaskCommand(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotNull(message = "Assignee id is required")
        UserId assigneeId
) {
    @Builder
    public CreateTaskCommand {
    }
}
