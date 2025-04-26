package com.task.management.domain.project.projection;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.TaskStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.notEmpty;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record ProjectDetails(
        ProjectId id,
        Instant createdAt,
        Instant updatedAt,
        String title,
        String description,
        MemberView owner,
        Set<TaskStatus> taskStatuses,
        Set<MemberView> members
) {

    @Builder
    public ProjectDetails(ProjectId id,
                          Instant createdAt,
                          Instant updatedAt,
                          String title,
                          String description,
                          MemberView owner,
                          Set<MemberView> members) {
        this(id, createdAt, updatedAt, title, description, owner, TaskStatus.all(), members);
    }

    public ProjectDetails {
        parameterRequired(id, "Project id");
        parameterRequired(createdAt, "Created time");
        notBlank(title, "Title");
        parameterRequired(owner, "Owner");
        notEmpty(taskStatuses, "Task status set");
        notEmpty(members, "Members");
    }
}
