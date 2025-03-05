package com.task.management.domain.project.port.in.command;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;
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
