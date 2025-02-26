package com.task.management.persistence.jpa;

import java.util.stream.Stream;

public interface IPage<T> {
    long total();
    int totalPages();
    int pageIndex();
    int pageSize();
    Stream<T> stream();
}
