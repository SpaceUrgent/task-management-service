package com.task.managment.web.project.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddTaskStatusRequest {
    @NotBlank(message = "Task status name is required")
    private String name;
    @NotNull(message = "Task status position is required")
    @Min(value = 1L, message = "Task status position must be greater than 0")
    private Integer position;
}
