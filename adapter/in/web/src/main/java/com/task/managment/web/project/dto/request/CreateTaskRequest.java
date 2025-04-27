package com.task.managment.web.project.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Assignee id is required")
    private Long assigneeId;
    @FutureOrPresent(message = "Due date must be present or future date")
    private LocalDate dueDate;
}
