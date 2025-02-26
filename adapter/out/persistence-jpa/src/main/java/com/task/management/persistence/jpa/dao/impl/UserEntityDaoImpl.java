package com.task.management.persistence.jpa.dao.impl;

import com.task.management.persistence.jpa.dao.AbstractEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.UserEntity;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class UserEntityDaoImpl extends AbstractEntityDao<UserEntity, Long> implements UserEntityDao {
    public UserEntityDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        emailRequired(email);
        final var query = entityManager.createQuery("from UserEntity user where user.email = :email", UserEntity.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }

    @Override
    public Optional<UserEntity> findMember(Long userId, Long projectId) {
        userIdRequired(userId);
        projectIdRequired(projectId);
        final var query = entityManager.createQuery(
                """
                from UserEntity user\s
                inner join user.projects project\s
                where user.id = :userId and project.id = :projectId
                """, UserEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("projectId", projectId);
        return query.getResultStream().findFirst();
    }

    @Override
    public List<UserEntity> findByProject(Long projectId) {
        projectIdRequired(projectId);
        final var query = entityManager.createQuery("""
                from UserEntity user\s
                inner join user.projects project\s
                where project.id = :projectId
                """, UserEntity.class);
        query.setParameter("projectId", projectId);
        return query.getResultList();
    }

    @Override
    public boolean existsByEmail(String email) {
        emailRequired(email);
        final var query = entityManager.createQuery("""
                select count(*) > 0
                from UserEntity user\s
                where user.email = :email
                """, boolean.class);
        query.setParameter("email", email);
        return query.getSingleResult();
    };

    @Override
    protected Class<UserEntity> entityClass() {
        return UserEntity.class;
    }

    private static void userIdRequired(Long userId) {
        requireNonNull(userId, "User id is required");
    }

    private static void projectIdRequired(Long projectId) {
        requireNonNull(projectId, "Project id is required");
    }

    private static void emailRequired(String email) {
        requireNonNull(email, "Email is required");
    }
}
