package com.task.managment.web.project.dto.request;

import com.task.management.domain.project.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Assignee id is required")
    private Long assigneeId;
    @NotNull(message = "Status is required")
    private TaskStatus status;
}
