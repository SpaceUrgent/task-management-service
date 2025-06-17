package com.task.management.persistence.jpa.mapper;

import com.task.management.application.project.projection.TaskChangeLogView;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.application.project.projection.TaskPreview;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.*;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.TaskNumber;
import com.task.management.persistence.jpa.entity.TaskChangeLogEntity;
import com.task.management.persistence.jpa.entity.TaskEntity;

import java.util.List;

import static com.task.management.domain.shared.model.objectvalue.TaskPriority.withOrder;
import static java.util.Objects.requireNonNull;

public class TaskMapper {
    public static final TaskMapper INSTANCE = new TaskMapper(UserInfoMapper.INSTANCE, TaskCommentMapper.INSTANCE);

    private final UserInfoMapper userInfoMapper;
    private final TaskCommentMapper taskCommentMapper;

    private TaskMapper(UserInfoMapper userInfoMapper,
                       TaskCommentMapper taskCommentMapper) {
        this.userInfoMapper = userInfoMapper;
        this.taskCommentMapper = taskCommentMapper;
    }

    public Task toTask(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return Task.builder()
                .id(getId(entity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .dueDate(entity.getDueDate())
                .number(new TaskNumber(entity.getNumber()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatusName())
                .project(getProjectId(entity))
                .priority(withOrder(entity.getPriority()))
                .owner(new UserId(entity.getOwner().getId()))
                .assignee(new UserId(entity.getAssignee().getId()))
                .build();
    }

    public TaskPreview toTaskPreview(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return TaskPreview.builder()
                .id(new TaskId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .dueDate(entity.getDueDate())
                .number(new TaskNumber(entity.getNumber()))
                .title(entity.getTitle())
                .status(entity.getStatus().getName())
                .priority(withOrder(entity.getPriority()))
                .assignee(userInfoMapper.toModel(entity.getAssignee()))
                .build();
    }

    public TaskDetails toTaskDetails(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return TaskDetails.builder()
                .id(new TaskId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .dueDate(entity.getDueDate())
                .number(new TaskNumber(entity.getNumber()))
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus().getName())
                .priority(withOrder(entity.getPriority()))
                .projectId(new ProjectId(entity.getProject().getId()))
                .owner(userInfoMapper.toModel(entity.getOwner()))
                .assignee(userInfoMapper.toModel(entity.getAssignee()))
                .changeLogs(toListModel(entity.getChangeLogs()))
                .comments(taskCommentMapper.toTaskCommentViews(entity.getComments()))
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

    private static TaskId getId(TaskEntity entity) {
        return new TaskId(entity.getId());
    }


    private static ProjectId getProjectId(TaskEntity entity) {
        return new ProjectId(entity.getProject().getId());
    }
}
