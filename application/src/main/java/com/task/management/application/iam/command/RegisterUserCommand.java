package com.task.management.application.iam.command;

import com.task.management.domain.common.model.objectvalue.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record RegisterUserCommand(
        @NotNull(message = "Email is required")
        Email email,
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

        @Override
        public String toString() {
                return "RegisterUserCommand{" +
                        "email=" + email +
                        ", firstName='" + firstName + '\'' +
                        ", lastName='" + lastName + '\'' +
                        '}';
        }
}
