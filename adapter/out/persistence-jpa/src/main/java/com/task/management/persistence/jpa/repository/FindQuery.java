package com.task.management.persistence.jpa.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import static java.util.Objects.requireNonNull;

public interface FindQuery<T> {
    CriteriaQuery<T> toQuery(CriteriaBuilder criteriaBuilder);

    default void criteriaBuilderRequired(CriteriaBuilder criteriaBuilder) {
        requireNonNull(criteriaBuilder, "Criteria builder is required");
    }
}
