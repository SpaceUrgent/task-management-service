package com.task.management.persistence.jpa.dao.impl;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.persistence.jpa.dao.AbstractEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.UserEntity;
import jakarta.persistence.EntityManager;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@AppComponent
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
    public boolean existsByEmail(String email) {
        emailRequired(email);
        final var query = entityManager.createQuery("""
                select count(*) > 0
                from UserEntity user\s
                where user.email = :email
                """, boolean.class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }

    @Override
    protected Class<UserEntity> entityClass() {
        return UserEntity.class;
    }

    private static void emailRequired(String email) {
        requireNonNull(email, "Email is required");
    }
}
