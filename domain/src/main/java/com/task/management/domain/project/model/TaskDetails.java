package com.task.management.domain.project.model;

import lombok.Builder;

import java.time.Instant;

import static com.task.management.domain.common.Validation.parameterRequired;

public record TaskDetails(
        TaskId id,
        Instant createdAt,
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
        parameterRequired(createdAt, "Created time");
        parameterRequired(projectId, "Project id");
        parameterRequired(title, "Title");
        parameterRequired(description, "Description");
        parameterRequired(status, "Status");
        parameterRequired(owner, "owner");
        parameterRequired(assignee, "assignee");
    }
}
