package com.task.management.application.dashboard.projection;

import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.common.model.objectvalue.*;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record DashboardTaskPreview(
        Instant createdAt,
        TaskId taskId,
        TaskNumber number,
        String title,
        ProjectId projectId,
        String projectTitle,
        LocalDate dueDate,
        Boolean isOverdue,
        TaskPriority priority,
        String status,
        UserInfo assignee
) {

    @Builder
    public DashboardTaskPreview {
        parameterRequired(createdAt, "Created at");
        parameterRequired(taskId, "Task id");
        parameterRequired(number, "Task number");
        notBlank(title, "Task title");
        parameterRequired(projectId, "Project id");
        notBlank(projectTitle, "Project title");
        parameterRequired(isOverdue, "Is overdue");
        parameterRequired(priority, "Task priority");
        parameterRequired(status, "Task status");
    }
}
