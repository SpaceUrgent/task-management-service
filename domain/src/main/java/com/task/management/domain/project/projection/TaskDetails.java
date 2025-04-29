package com.task.management.domain.project.projection;

import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.project.model.*;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record TaskDetails(
        TaskId id,
        TaskNumber number,
        Instant createdAt,
        Instant updatedAt,
        LocalDate dueDate,
        ProjectId projectId,
        String title,
        String description,
        TaskStatus status,
        UserInfo owner,
        UserInfo assignee,
        List<TaskChangeLogView> changeLogs
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
        Optional.ofNullable(changeLogs).orElseGet(ArrayList::new);
    }
}
