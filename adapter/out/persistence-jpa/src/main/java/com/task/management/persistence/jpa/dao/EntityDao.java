package com.task.management.persistence.jpa.dao;

import com.task.management.persistence.jpa.IPage;
import com.task.management.persistence.jpa.entity.JpaEntity;
import com.task.management.persistence.jpa.query.FindPageQuery;

import java.util.List;
import java.util.Optional;

public interface EntityDao<T extends JpaEntity<ID>, ID> {
    Optional<T> findById(ID id);

    List<T> findAll();

    IPage<T> findPage(FindPageQuery<T> query);

    T getReference(ID id);

    T save(T entity);
}
