package com.task.management.application.project.projection;

import com.task.management.domain.project.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.task.management.domain.common.validation.Validation.*;

public record ProjectDetails(
        ProjectId id,
        Instant createdAt,
        Instant updatedAt,
        String title,
        String description,
        MemberView owner,
        List<TaskStatus> taskStatuses,
        Set<MemberView> members
) {

    @Builder
    public ProjectDetails {
        parameterRequired(id, "Project id");
        parameterRequired(createdAt, "Created time");
        notBlank(title, "Title");
        parameterRequired(owner, "Owner");
        notEmpty(taskStatuses, "Task status set");
        notEmpty(members, "Members");
    }
}
