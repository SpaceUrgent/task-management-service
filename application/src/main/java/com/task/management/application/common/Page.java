package com.task.management.application.common;

import lombok.Builder;

import java.util.List;

import static com.task.management.application.common.Validation.parameterRequired;

public record Page<T>(Integer pageNo,
                      Integer pageSize,
                      Integer total,
                      Integer totalPages,
                      List<T> content) {
    @Builder
    public Page {
        parameterRequired(pageNo, "Page number");
        parameterRequired(pageSize, "Page size");
        parameterRequired(total, "Total");
        parameterRequired(totalPages, "Total pages");
        parameterRequired(content, "Content");
    }
}
