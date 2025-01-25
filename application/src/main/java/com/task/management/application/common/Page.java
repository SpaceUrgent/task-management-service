package com.task.management.application.common;

import lombok.Data;

import static java.util.Objects.requireNonNull;

@Data
public class Page {
    private final int pageNumber;
    private final int pageSize;

    public Page(Integer pageNumber, Integer pageSize) {
        this.pageNumber = requireNonNull(pageNumber, "Page number is required");
        this.pageSize = requireNonNull(pageSize, "Page number is required");
    }
}
