package com.task.management.application.iam.port.in.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record RegisterUserCommand(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @NotNull(message = "Password is required")
        @Size(min = 6, message = "Password must contain at least 6 characters")
        char[] password
) {
        @Builder
        public RegisterUserCommand {
        }
}
