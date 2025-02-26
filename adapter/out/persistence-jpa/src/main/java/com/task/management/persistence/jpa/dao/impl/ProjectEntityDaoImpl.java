package com.task.management.persistence.jpa.dao.impl;

import com.task.management.persistence.jpa.dao.AbstractEntityDao;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import jakarta.persistence.EntityManager;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class ProjectEntityDaoImpl extends AbstractEntityDao<ProjectEntity, Long> implements ProjectEntityDao {
    public ProjectEntityDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Stream<ProjectEntity> findByMemberId(Long memberId) {
        requireNonNull(memberId, "Member id is required");
        return entityManager.createQuery("""
                from ProjectEntity project\s
                inner join project.members member\s
                where member.id = :memberId
                """, ProjectEntity.class)
                .setParameter("memberId", memberId)
                .getResultStream();
    }

    @Override
    protected Class<ProjectEntity> entityClass() {
        return ProjectEntity.class;
    }
}
