package com.task.management.domain.project.port.in.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record UpdateProjectCommand(
        @NotBlank(message = "Title is required")
        String title,
        String description
) {
    @Builder
    public UpdateProjectCommand {
    }
}
