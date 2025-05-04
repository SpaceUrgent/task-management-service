package com.task.management.application.project.projection;

import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.project.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskId;
import com.task.management.domain.project.model.objectvalue.TaskNumber;
import com.task.management.domain.project.model.objectvalue.TaskStatus;
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
        String status,
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
        notBlank(status, "Status");
        parameterRequired(owner, "owner");
        parameterRequired(assignee, "assignee");
        Optional.ofNullable(changeLogs).orElseGet(ArrayList::new);
    }
}
