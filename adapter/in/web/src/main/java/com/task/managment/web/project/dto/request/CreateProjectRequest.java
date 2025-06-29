package com.task.managment.web.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProjectRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
}
