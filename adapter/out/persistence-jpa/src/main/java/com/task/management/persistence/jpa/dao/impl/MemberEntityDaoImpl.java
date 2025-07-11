package com.task.management.persistence.jpa.dao.impl;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.persistence.jpa.dao.MemberEntityDao;
import com.task.management.persistence.jpa.entity.MemberEntity;
import jakarta.persistence.EntityManager;

import java.util.Optional;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@AppComponent
public class MemberEntityDaoImpl implements MemberEntityDao {
    private final EntityManager entityManager;

    public MemberEntityDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<MemberEntity> findById(MemberEntity.MemberPK id) {
        parameterRequired(id, "Member id");
        return Optional.ofNullable(entityManager.find(MemberEntity.class, id));
    }

    @Override
    public MemberEntity save(MemberEntity entity) {
        memberEntityRequired(entity);
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return entity;
    }

    @Override
    public void delete(MemberEntity entity) {
        memberEntityRequired(entity);
        entityManager.remove(entity);
    }

    private static void memberEntityRequired(MemberEntity entity) {
        parameterRequired(entity, "Entity");
    }
}
