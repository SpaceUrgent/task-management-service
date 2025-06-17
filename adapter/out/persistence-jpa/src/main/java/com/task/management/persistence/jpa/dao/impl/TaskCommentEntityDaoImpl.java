package com.task.management.persistence.jpa.dao.impl;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.persistence.jpa.dao.AbstractEntityDao;
import com.task.management.persistence.jpa.dao.TaskCommentEntityDao;
import com.task.management.persistence.jpa.entity.TaskCommentEntity;
import jakarta.persistence.EntityManager;

@AppComponent
public class TaskCommentEntityDaoImpl extends AbstractEntityDao<TaskCommentEntity, Long> implements TaskCommentEntityDao {
    public TaskCommentEntityDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<TaskCommentEntity> entityClass() {
        return TaskCommentEntity.class;
    }
}
