package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.*;
import com.task.management.domain.project.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskId;
import com.task.management.domain.project.model.objectvalue.TaskNumber;
import com.task.management.persistence.jpa.entity.TaskEntity;

import static java.util.Objects.requireNonNull;

public class TaskMapper {
    public static final TaskMapper INSTANCE = new TaskMapper();

    private TaskMapper() {
    }

    public Task toModel(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return Task.builder()
                .id(getId(entity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .dueDate(entity.getDueDate())
                .number(new TaskNumber(entity.getNumber()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .project(getProjectId(entity))
                .owner(new UserId(entity.getOwner().getId()))
                .assignee(new UserId(entity.getAssignee().getId()))
                .build();
    }

    private static TaskId getId(TaskEntity entity) {
        return new TaskId(entity.getId());
    }


    private static ProjectId getProjectId(TaskEntity entity) {
        return new ProjectId(entity.getProject().getId());
    }
}
