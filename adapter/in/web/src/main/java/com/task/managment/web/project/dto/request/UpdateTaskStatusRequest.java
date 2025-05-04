package com.task.managment.web.project.dto.request;

import com.task.management.domain.project.model.objectvalue.TaskStatusOld;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskStatusRequest {
    @NotNull(message = "Status is required")
    private TaskStatusOld status;
}
