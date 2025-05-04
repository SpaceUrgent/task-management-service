package com.task.management.persistence.jpa.project.mapper;

import com.task.management.application.project.projection.TaskChangeLogView;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.domain.project.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskId;
import com.task.management.domain.project.model.objectvalue.TaskNumber;
import com.task.management.persistence.jpa.common.mapper.UserInfoMapper;
import com.task.management.persistence.jpa.entity.TaskChangeLogEntity;
import com.task.management.persistence.jpa.entity.TaskEntity;

import java.util.List;

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
                .changeLogs(toListModel(entity.getChangeLogs()))
                .build();
    }

    private List<TaskChangeLogView> toListModel(List<TaskChangeLogEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }

    private TaskChangeLogView toModel(TaskChangeLogEntity entity) {
        return TaskChangeLogView.builder()
                .time(entity.getOccurredAt())
                .actor(userInfoMapper.toModel(entity.getActor()))
                .targetProperty(entity.getTaskProperty())
                .initialValue(entity.getOldValue())
                .newValue(entity.getNewValue())
                .build();
    }
}
