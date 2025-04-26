package com.task.management.domain.project.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record CreateProjectCommand(
        @NotBlank(message = "Title is required")
        String title,
        String description
) {
    @Builder
    public CreateProjectCommand {
    }
}
