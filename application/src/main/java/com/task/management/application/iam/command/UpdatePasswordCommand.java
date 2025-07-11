package com.task.management.application.iam.command;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record UpdatePasswordCommand(
        @NotNull(message = "Current password is required")
        char[] currentPassword,
        @NotNull(message = "New password is required")
        char[] newPassword
) {

    @Builder
    public UpdatePasswordCommand {
    }
}
