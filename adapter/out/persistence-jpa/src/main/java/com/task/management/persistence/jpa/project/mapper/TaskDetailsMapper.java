package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.projection.TaskDetails;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskNumber;
import com.task.management.persistence.jpa.common.mapper.UserInfoMapper;
import com.task.management.persistence.jpa.entity.TaskEntity;

import static java.util.Objects.requireNonNull;

public class TaskDetailsMapper {
    public static final TaskDetailsMapper INSTANCE = new TaskDetailsMapper(UserInfoMapper.INSTANCE);

    private final UserInfoMapper userInfoMapper;

    private TaskDetailsMapper(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    public TaskDetails toModel(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return TaskDetails.builder()
                .id(new TaskId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .dueDate(entity.getDueDate())
                .number(new TaskNumber(entity.getNumber()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .projectId(new ProjectId(entity.getProject().getId()))
                .owner(userInfoMapper.toModel(entity.getOwner()))
                .assignee(userInfoMapper.toModel(entity.getAssignee()))
                .build();
    }
}
