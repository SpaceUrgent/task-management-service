package com.task.managment.web.project.dto.request;

import com.task.management.domain.project.model.objectvalue.TaskPriority;
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
    @NotNull(message = "Assignee id is required")
    private Long assigneeId;
    @NotNull(message = "Status is required")
    private String status;
    @FutureOrPresent(message = "Due date must be present or future date")
    private LocalDate dueDate;
    @NotNull(message = "Task prioruty is required")
    private TaskPriority priority;
}
