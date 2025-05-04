package com.task.management.application.project.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record AddTaskStatusCommand(
        @NotBlank(message = "Status name is required")
        String name,
        @NotNull(message = "Status position is required")
        Integer position
) {

        @Builder
        public AddTaskStatusCommand {
        }
}
