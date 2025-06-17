package com.task.management.persistence.jpa.project.mapper;

import com.task.management.application.project.projection.TaskCommentView;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.TaskComment;
import com.task.management.domain.project.model.objectvalue.TaskCommentId;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.persistence.jpa.common.mapper.UserInfoMapper;
import com.task.management.persistence.jpa.entity.TaskCommentEntity;

import java.util.Collection;
import java.util.List;

public class TaskCommentMapper {
    public static final TaskCommentMapper INSTANCE = new TaskCommentMapper(UserInfoMapper.INSTANCE);

    private final UserInfoMapper userInfoMapper;

    public TaskCommentMapper(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    public TaskComment toTaskComment(TaskCommentEntity entity) {
        return TaskComment.builder()
                .id(new TaskCommentId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .author(new UserId(entity.getAuthor().getId()))
                .task(new TaskId(entity.getTask().getId()))
                .content(entity.getContent())
                .build();
    }

    public List<TaskCommentView> toTaskCommentViews(Collection<TaskCommentEntity> entities) {
        return entities.stream()
                .map(this::toTaskCommentView)
                .toList();
    }

    public TaskCommentView toTaskCommentView(TaskCommentEntity entity) {
        return TaskCommentView.builder()
                .id(new TaskCommentId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .author(userInfoMapper.toModel(entity.getAuthor()))
                .content(entity.getContent())
                .build();
    }
}
