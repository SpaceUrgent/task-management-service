package com.task.management.persistence.jpa.dao.impl;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.persistence.jpa.dao.AbstractEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.entity.TaskEntity;
import jakarta.persistence.EntityManager;

@AppComponent
public class TaskEntityDaoImpl extends AbstractEntityDao<TaskEntity, Long> implements TaskEntityDao {
    public TaskEntityDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<TaskEntity> entityClass() {
        return TaskEntity.class;
    }
}
