package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskNumber;
import com.task.management.domain.project.projection.TaskPreview;
import com.task.management.persistence.jpa.common.mapper.UserInfoMapper;
import com.task.management.persistence.jpa.entity.TaskEntity;

import static java.util.Objects.requireNonNull;

public class TaskPreviewMapper {
    public static final TaskPreviewMapper INSTANCE = new TaskPreviewMapper(UserInfoMapper.INSTANCE);

    private final UserInfoMapper userInfoMapper;

    private TaskPreviewMapper(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    public TaskPreview toModel(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return TaskPreview.builder()
                .id(new TaskId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .dueDate(entity.getDueDate())
                .number(new TaskNumber(entity.getNumber()))
                .title(entity.getTitle())
                .status(entity.getStatus())
                .assignee(userInfoMapper.toModel(entity.getAssignee()))
                .build();
    }
}
