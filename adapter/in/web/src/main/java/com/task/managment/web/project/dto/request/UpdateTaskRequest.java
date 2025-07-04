package com.task.managment.web.project.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private Long assigneeId;
    @NotNull(message = "Status is required")
    private String status;
    @FutureOrPresent(message = "Due date must be present or future date")
    private LocalDate dueDate;
    @NotBlank(message = "Task priority is required")
    private String priority;
}
