package com.task.management.persistence.jpa.pagination;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class JpaPageImpl<T> implements JpaPage<T> {
    private final int pageIndex;
    private final int pageSize;
    private final long total;
    private final Collection<T> content;

    public JpaPageImpl(Integer pageIndex,
                       Integer pageSize,
                       Long total,
                       Collection<T> content) {
        this.pageIndex = requireNonNull(pageIndex, "Page index is required");
        this.pageSize = requireNonNull(pageSize, "Page size is required");
        this.total = requireNonNull(total, "Total is required");
        this.content = requireNonNull(content, "Content is required");
    }

    @Override
    public long total() {
        return this.total;
    }

    @Override
    public int totalPages() {
        return this.pageSize() == 0 ? 1 : (int) Math.ceil((double) this.total / (double) this.pageSize());
    }

    @Override
    public int pageIndex() {
        return this.pageIndex;
    }

    @Override
    public int pageSize() {
        return this.pageSize;
    }

    @Override
    public Stream<T> stream() {
        return content.stream();
    }
}
