package com.task.management.persistence.jpa.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

public interface FindPageQuery<T> extends FindQuery<T> {
    CriteriaQuery<Long> toCountQuery(CriteriaBuilder criteriaBuilder);

    int pageIndex();

    int size();

    default int offset() {
        return pageIndex() * size();
    }
}
