package com.task.management.persistence.jpa.pagination;

import java.util.stream.Stream;

public interface JpaPage<T> {
    long total();
    int totalPages();
    int pageIndex();
    int pageSize();
    Stream<T> stream();
}
