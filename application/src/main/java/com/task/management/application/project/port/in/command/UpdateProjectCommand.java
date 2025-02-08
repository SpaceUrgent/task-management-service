package com.task.management.application.port.in.command;

import com.task.management.application.project.model.ProjectId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record UpdateProjectCommand(
        @NotNull(message = "Project id is required")
        ProjectId projectId,
        @NotBlank(message = "Title is required")
        String title,
        String description
) {
    @Builder
    public UpdateProjectCommand {
    }
}
