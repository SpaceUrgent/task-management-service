package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.model.TaskId;
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
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .project(getProjectId(entity))
                .owner(new ProjectUserId(entity.getOwner().getId()))
                .assignee(new ProjectUserId(entity.getAssignee().getId()))
                .build();
    }

    private static TaskId getId(TaskEntity entity) {
        return new TaskId(entity.getId());
    }


    private static ProjectId getProjectId(TaskEntity entity) {
        return new ProjectId(entity.getProject().getId());
    }
}
