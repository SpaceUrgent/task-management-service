package com.task.managment.web.iam.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    @NotNull(message = "Old password is required")
    private char[] oldPassword;
    @NotNull(message = "New password is required")
    @Size(min = 8, message = "New password must contain at least 8 symbols")
    private char[] newPassword;
}
