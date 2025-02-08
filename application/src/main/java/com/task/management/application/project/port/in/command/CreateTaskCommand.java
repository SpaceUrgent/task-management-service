package com.task.management.application.project.port.in.command;

import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUserId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record CreateTaskCommand(
        @NotNull(message = "Project id is required")
        ProjectId projectId,
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotBlank(message = "Assignee id is required")
        ProjectUserId assigneeId
) {
    @Builder
    public CreateTaskCommand {
    }
}
