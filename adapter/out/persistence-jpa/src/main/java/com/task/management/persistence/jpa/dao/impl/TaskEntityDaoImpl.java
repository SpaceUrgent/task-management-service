package com.task.management.persistence.jpa.dao.impl;

import com.task.management.application.common.annotation.AppComponent;
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

    @Override
    public boolean existsWithProjectIdAndStatus(Long projectId, String statusName) {
        final var query = entityManager.createQuery("""
                select count (*) > 0 from TaskEntity t\s
                inner join t.project p\s
                where p.id = :projectId\s
                and t.status = :statusName
                """, boolean.class);
        query.setParameter("projectId", projectId);
        query.setParameter("statusName", statusName);
        return query.getSingleResult();
    }
}
