package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.projection.TaskDetails;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskNumber;
import com.task.management.persistence.jpa.entity.TaskEntity;

import static java.util.Objects.requireNonNull;

public class TaskDetailsMapper {
    public static final TaskDetailsMapper INSTANCE = new TaskDetailsMapper(ProjectUserMapper.INSTANCE);

    private final ProjectUserMapper projectUserMapper;

    private TaskDetailsMapper(ProjectUserMapper projectUserMapper) {
        this.projectUserMapper = projectUserMapper;
    }

    public TaskDetails toModel(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return TaskDetails.builder()
                .id(new TaskId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .number(new TaskNumber(entity.getNumber()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .projectId(new ProjectId(entity.getProject().getId()))
                .owner(projectUserMapper.toModel(entity.getOwner()))
                .assignee(projectUserMapper.toModel(entity.getAssignee()))
                .build();
    }
}
