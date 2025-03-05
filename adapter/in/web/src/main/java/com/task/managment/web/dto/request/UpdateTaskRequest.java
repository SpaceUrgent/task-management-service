package com.task.managment.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
}
