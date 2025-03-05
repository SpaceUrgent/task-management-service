package com.task.management.domain.port.in.command;

import com.task.management.domain.project.model.ProjectId;
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
