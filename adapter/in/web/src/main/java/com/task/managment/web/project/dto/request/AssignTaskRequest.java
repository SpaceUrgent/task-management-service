package com.task.managment.web.project.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignTaskRequest {
    @NotNull
    private Long assigneeId;
}
