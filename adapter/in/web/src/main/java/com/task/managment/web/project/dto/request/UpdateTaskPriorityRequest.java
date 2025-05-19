package com.task.managment.web.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTaskPriorityRequest {
    @NotBlank(message = "Task priority is required")
    private String priority;
}
