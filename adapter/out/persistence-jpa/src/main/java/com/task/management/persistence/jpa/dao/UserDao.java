package com.task.management.persistence.jpa.dao;

import com.task.management.persistence.jpa.entity.UserEntity;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class UserDao {
    private final EntityManager entityManager;

    public UserDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public UserEntity save(UserEntity entity) {
        requireNonNull(entity, "Entity is required");
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return entity;
    }

    public Optional<UserEntity> findById(Long id) {
        userIdRequired(id);
        return Optional.ofNullable(entityManager.find(UserEntity.class, id));
    }

    public Optional<UserEntity> findByEmail(String email) {
        emailRequired(email);
        final var query = entityManager.createQuery("from UserEntity user where user.email = :email", UserEntity.class);
        query.setParameter("email", email);
        final var result = query.getSingleResult();
        return Optional.ofNullable(result);
    }

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
        final var result = query.getSingleResult();
        return Optional.ofNullable(result);
    }

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
