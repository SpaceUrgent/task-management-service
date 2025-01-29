package com.task.managment.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailDto {
    @NotBlank(message = "Email is required")
    private String email;
}
