package com.task.management.persistence.jpa.dao.impl;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.persistence.jpa.dao.AbstractEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.view.TasksSummaryView;
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
                and t.status.id.name = :statusName
                """, boolean.class);
        query.setParameter("projectId", projectId);
        query.setParameter("statusName", statusName);
        return query.getSingleResult();
    }

    @Override
    public TasksSummaryView getSummaryByAssigneeId(Long assigneeId) {
        final var query = entityManager.createQuery("""
                select\s
                count(t) as total,
                sum(case when t.status.isFinal = false then 1 else 0 end) as open,\s
                sum(case when t.dueDate is not null and t.dueDate < current_date then 1 else 0 end) as overdue,
                sum(case when t.status.isFinal = true then 1 else 0 end) as closed\s
                from TaskEntity t\s
                where t.assignee.id = :assigneeId
                """);
        query.setParameter("assigneeId", assigneeId);
        final var result = query.getSingleResult();
        return toTaskSummaryView(result);
    }

    @Override
    public TasksSummaryView getSummaryByOwnerId(Long ownerId) {
        final var query = entityManager.createQuery("""
                select\s
                count(t) as total,
                sum(case when t.status.isFinal = false then 1 else 0 end) as open,\s
                sum(case when t.dueDate is not null and t.dueDate < current_date then 1 else 0 end) as overdue,
                sum(case when t.status.isFinal = true then 1 else 0 end) as closed\s
                from TaskEntity t\s
                where t.owner.id = :ownerId
                """);
        query.setParameter("ownerId", ownerId);
        final var result = query.getSingleResult();
        return toTaskSummaryView(result);
    }

    private TasksSummaryView toTaskSummaryView(Object source) {
        final var values = (Object[]) source;
        return TasksSummaryView.builder()
                .total(asLong(values[0]).intValue())
                .open(asLong(values[1]).intValue())
                .overdue(asLong(values[2]).intValue())
                .closed(asLong(values[3]).intValue())
                .build();
    }

    private Long asLong(Object source) {
        return (Long) source;
    }
}
