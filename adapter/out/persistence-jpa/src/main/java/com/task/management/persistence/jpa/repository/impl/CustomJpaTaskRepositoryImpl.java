package com.task.management.persistence.jpa.repository.impl;

import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.repository.CustomJpaTaskRepository;
import com.task.management.persistence.jpa.repository.FindPageQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static java.util.Objects.requireNonNull;
import static org.springframework.data.domain.PageRequest.of;

@RequiredArgsConstructor
public class CustomJpaTaskRepositoryImpl implements CustomJpaTaskRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Page<TaskEntity> findPage(FindPageQuery<TaskEntity> query) {
        requireNonNull(query, "Query is required");
        final var criteriaBuilder = entityManager.getCriteriaBuilder();
        final var content = entityManager.createQuery(query.toQuery(criteriaBuilder))
                .setFirstResult(query.offset())
                .setMaxResults(query.size())
                .getResultList();
        final var total = entityManager.createQuery(query.toCountQuery(criteriaBuilder))
                .getSingleResult();
        return new PageImpl<>(content, of(query.pageIndex(), query.size()), total);
    }
}
