package com.task.management.persistence.jpa.project.mapper;

import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.TaskComment;
import com.task.management.domain.project.model.objectvalue.TaskCommentId;
import com.task.management.domain.project.model.objectvalue.TaskId;
import com.task.management.persistence.jpa.entity.TaskCommentEntity;

public class TaskCommentMapper {
    public static final TaskCommentMapper INSTANCE = new TaskCommentMapper();

    public TaskComment toTaskComment(TaskCommentEntity entity) {
        return TaskComment.builder()
                .id(new TaskCommentId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .author(new UserId(entity.getAuthor().getId()))
                .task(new TaskId(entity.getTask().getId()))
                .content(entity.getContent())
                .build();
    }
}
