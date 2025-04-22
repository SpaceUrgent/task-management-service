package com.task.management.domain.project.model;

import lombok.Builder;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record TaskDetails(
        TaskId id,
        TaskNumber number,
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
        parameterRequired(number, "Number");
        parameterRequired(createdAt, "Created time");
        parameterRequired(projectId, "Project id");
        notBlank(title, "Title");
        parameterRequired(status, "Status");
        parameterRequired(owner, "owner");
        parameterRequired(assignee, "assignee");
    }
}
