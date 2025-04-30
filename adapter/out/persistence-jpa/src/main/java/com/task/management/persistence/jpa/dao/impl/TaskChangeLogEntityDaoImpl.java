package com.task.management.persistence.jpa.dao.impl;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.persistence.jpa.dao.AbstractEntityDao;
import com.task.management.persistence.jpa.dao.TaskChangeLogEntityDao;
import com.task.management.persistence.jpa.entity.TaskChangeLogEntity;
import jakarta.persistence.EntityManager;

@AppComponent
public class TaskChangeLogEntityDaoImpl extends AbstractEntityDao<TaskChangeLogEntity, Long> implements TaskChangeLogEntityDao {

    public TaskChangeLogEntityDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<TaskChangeLogEntity> entityClass() {
        return TaskChangeLogEntity.class;
    }
}
