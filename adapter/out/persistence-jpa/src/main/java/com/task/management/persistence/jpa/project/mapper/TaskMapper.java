package com.task.management.persistence.jpa.mapper;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskStatus;
import com.task.management.persistence.jpa.entity.TaskEntity;

import static java.util.Objects.requireNonNull;

public class TaskMapper {
    private final ProjectUserMapper projectUserMapper;

    TaskMapper(ProjectUserMapper projectUserMapper) {
        this.projectUserMapper = projectUserMapper;
    }

    public Task toModel(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return Task.builder()
                .id(getId(entity))
                .createdAt(entity.getCreatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(getTaskStatus(entity))
                .project(getProjectId(entity))
                .owner(projectUserMapper.toModel(entity.getOwner()))
                .assignee(projectUserMapper.toModel(entity.getAssignee()))
                .build();
    }

    private static TaskId getId(TaskEntity entity) {
        return new TaskId(entity.getId());
    }

    private static TaskStatus getTaskStatus(TaskEntity entity) {
        return new TaskStatus(entity.getStatus());
    }

    private static ProjectId getProjectId(TaskEntity entity) {
        return new ProjectId(entity.getProject().getId());
    }
}
