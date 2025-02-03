package com.task.management.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTaskDTO {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Assignee id is required")
    private Long assigneeId;
}
