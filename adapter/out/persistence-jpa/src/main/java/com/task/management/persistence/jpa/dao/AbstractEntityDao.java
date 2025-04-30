package com.task.management.persistence.jpa.dao;

import com.task.management.persistence.jpa.IPage;
import com.task.management.persistence.jpa.PageImpl;
import com.task.management.persistence.jpa.entity.JpaEntity;
import com.task.management.persistence.jpa.query.FindPageQuery;
import jakarta.persistence.EntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.parameterRequired;
import static java.util.Objects.requireNonNull;

public abstract class AbstractEntityDao<T extends JpaEntity<ID>, ID> implements EntityDao<T, ID> {
    protected final EntityManager entityManager;

    protected AbstractEntityDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<T> findById(ID id) {
        idRequired(id);
        return Optional.ofNullable(entityManager.find(entityClass(), id));
    }

    @Override
    public Optional<T> findById(ID id, String entityGraphName) {
        idRequired(id);

        final var hints = new HashMap<String, Object>();

        if (entityGraphName != null && !entityGraphName.isBlank()) {
            hints.put("jakarta.persistence.fetchgraph", entityManager.getEntityGraph(entityGraphName));
        }

        return Optional.ofNullable(entityManager.find(entityClass(), id, hints));
    }

    @Override
    public List<T> findAll() {
        final var query = entityManager.createQuery(
                "from %s".formatted(entityClass().getSimpleName()),
                entityClass()
        );
        return query.getResultList();
    }

    @Override
    public IPage<T> findPage(FindPageQuery<T> query) {
        final var criteriaBuilder = entityManager.getCriteriaBuilder();
        final var content = entityManager.createQuery(query.toQuery(criteriaBuilder))
                .setFirstResult(query.offset())
                .setMaxResults(query.size())
                .getResultList();
        final var total = entityManager.createQuery(query.toCountQuery(criteriaBuilder))
                .getSingleResult();
        return new PageImpl<>(query.pageIndex(), query.size(), total, content);
    }

    public T getReference(ID id) {
        idRequired(id);
        return entityManager.getReference(entityClass(), id);
    }

    @Override
    public T save(T entity) {
        parameterRequired(entity, "Entity");
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
        return entity;
    }

    protected abstract Class<T> entityClass();

    protected static <ID> void idRequired(ID id) {
        requireNonNull(id, "Entity id is required");
    }
}
