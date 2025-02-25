package com.task.management.persistence.jpa.mapper;

import com.task.management.application.project.model.TaskId;
import com.task.management.application.project.model.TaskPreview;
import com.task.management.application.project.model.TaskStatus;
import com.task.management.persistence.jpa.entity.TaskEntity;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class TaskPreviewMapper {
    private final ProjectUserMapper projectUserMapper;

    public TaskPreview toModel(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return TaskPreview.builder()
                .id(new TaskId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .title(entity.getTitle())
                .status(new TaskStatus(entity.getStatus()))
                .assignee(projectUserMapper.toModel(entity.getAssignee()))
                .build();
    }
}
