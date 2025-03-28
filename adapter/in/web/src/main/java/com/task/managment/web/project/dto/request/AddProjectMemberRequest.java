package com.task.managment.web.project.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddProjectMemberRequest {
    @Email(message = "Invalid email value", regexp = com.task.management.domain.common.Email.VALID_EMAIL_REGEXP)
    @NotBlank(message = "Email is required")
    private String email;
}
