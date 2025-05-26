package com.task.management.application.iam.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record UpdateNameCommand(
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName
) {

    @Builder
    public UpdateNameCommand {
    }
}
