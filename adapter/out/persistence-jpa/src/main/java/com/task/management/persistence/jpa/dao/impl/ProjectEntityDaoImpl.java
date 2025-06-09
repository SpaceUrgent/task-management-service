package com.task.management.persistence.jpa.dao.impl;

import com.task.management.application.common.annotation.AppComponent;
import com.task.management.persistence.jpa.dao.AbstractEntityDao;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.TaskStatusEntity;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Stream;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@AppComponent
public class ProjectEntityDaoImpl extends AbstractEntityDao<ProjectEntity, Long> implements ProjectEntityDao {
    public ProjectEntityDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Stream<ProjectEntity> findByMemberId(Long memberId) {
        parameterRequired(memberId, "Member id");
        return entityManager.createQuery("""
                from ProjectEntity project\s
                inner join project.members member\s
                where member.user.id = :memberId
                """, ProjectEntity.class)
                .setParameter("memberId", memberId)
                .getResultStream();
    }

    @Override
    public List<TaskStatusEntity> findAvailableTaskStatuses(Long projectId) {
        parameterRequired(projectId, "Project id");
        return entityManager.createQuery("""
                select p.availableTaskStatuses from ProjectEntity p\s
                where p.id = :projectId
                """, TaskStatusEntity.class)
                .setParameter("projectId", projectId)
                .getResultList();
    }

    @Override
    protected Class<ProjectEntity> entityClass() {
        return ProjectEntity.class;
    }
}
