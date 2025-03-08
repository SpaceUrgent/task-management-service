package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskPreview;
import com.task.management.persistence.jpa.entity.TaskEntity;

import static java.util.Objects.requireNonNull;

public class TaskPreviewMapper {
    public static final TaskPreviewMapper INSTANCE = new TaskPreviewMapper(ProjectUserMapper.INSTANCE);

    private final ProjectUserMapper projectUserMapper;

    private TaskPreviewMapper(ProjectUserMapper projectUserMapper) {
        this.projectUserMapper = projectUserMapper;
    }

    public TaskPreview toModel(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return TaskPreview.builder()
                .id(new TaskId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .title(entity.getTitle())
                .status(entity.getStatus())
                .assignee(projectUserMapper.toModel(entity.getAssignee()))
                .build();
    }
}
