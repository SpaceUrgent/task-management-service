package com.task.managment.web.project.dto.request;

import com.task.management.domain.project.model.objectvalue.TaskPriority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskPriorityRequest {
    @NotNull(message = "Task priority is required")
    private TaskPriority priority;
}
