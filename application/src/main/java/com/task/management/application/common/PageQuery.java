package com.task.management.application.common;

import lombok.Data;

import static java.util.Objects.requireNonNull;

@Data
public class PageQuery {
    private final int pageNumber;
    private final int pageSize;

    public PageQuery(Integer pageNumber, Integer pageSize) {
        this.pageNumber = requireNonNull(pageNumber, "Page number is required");
        this.pageSize = requireNonNull(pageSize, "Page number is required");
    }
}
