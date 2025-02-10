package com.task.management.application.project.model;

import lombok.Builder;

import java.time.Instant;

import static com.task.management.application.common.Validation.parameterRequired;

public record TaskDetails(
        TaskId id,
        Instant createdTime,
        ProjectId projectId,
        String title,
        String description,
        TaskStatus status,
        ProjectUser owner,
        ProjectUser assignee
) {
    @Builder
    public TaskDetails {
        parameterRequired(id, "Id");
        parameterRequired(createdTime, "Created time");
        parameterRequired(projectId, "Project id");
        parameterRequired(title, "Title");
        parameterRequired(description, "Description");
        parameterRequired(status, "Status");
        parameterRequired(owner, "owner");
        parameterRequired(assignee, "assignee");
    }
}
