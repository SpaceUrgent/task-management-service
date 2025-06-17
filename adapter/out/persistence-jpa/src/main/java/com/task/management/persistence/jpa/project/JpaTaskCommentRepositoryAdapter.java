package com.task.management.persistence.jpa.project;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.project.port.out.TaskCommentRepositoryPort;
import com.task.management.domain.project.model.TaskComment;
import com.task.management.domain.project.model.objectvalue.TaskCommentId;
import com.task.management.persistence.jpa.dao.TaskCommentEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.TaskCommentEntity;
import com.task.management.persistence.jpa.project.mapper.TaskCommentMapper;

import java.util.Optional;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@AppComponent
public class JpaTaskCommentRepositoryAdapter implements TaskCommentRepositoryPort {
    private final TaskCommentEntityDao taskCommentEntityDao;
    private final TaskEntityDao taskEntityDao;
    private final UserEntityDao userEntityDao;
    private final TaskCommentMapper taskCommentMapper = TaskCommentMapper.INSTANCE;

    public JpaTaskCommentRepositoryAdapter(TaskCommentEntityDao taskCommentEntityDao,
                                           TaskEntityDao taskEntityDao,
                                           UserEntityDao userEntityDao) {
        this.taskCommentEntityDao = taskCommentEntityDao;
        this.taskEntityDao = taskEntityDao;
        this.userEntityDao = userEntityDao;
    }

    @Override
    public TaskComment save(TaskComment comment) {
        parameterRequired(comment, "Task comment");
        var taskCommentEntity = TaskCommentEntity.builder()
                .id(Optional.ofNullable(comment.getId()).map(TaskCommentId::value).orElse(null))
                .createdAt(comment.getCreatedAt())
                .author(userEntityDao.getReference(comment.getAuthor().value()))
                .task(taskEntityDao.getReference(comment.getTask().value()))
                .content(comment.getContent())
                .build();
        taskCommentEntity = taskCommentEntityDao.save(taskCommentEntity);
        return taskCommentMapper.toTaskComment(taskCommentEntity);
    }
}
