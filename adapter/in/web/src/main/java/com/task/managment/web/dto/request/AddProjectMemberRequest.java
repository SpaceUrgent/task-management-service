package com.task.managment.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddProjectMemberRequest {
    @Email(message = "Invalid email value")
    @NotBlank(message = "Email is required")
    private String email;
}
