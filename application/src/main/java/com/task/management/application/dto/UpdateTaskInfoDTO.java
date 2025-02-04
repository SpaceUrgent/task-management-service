package com.task.management.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTaskInfoDTO {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
}
