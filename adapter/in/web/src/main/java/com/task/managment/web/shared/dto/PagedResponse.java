package com.task.managment.web.shared.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Data
@NoArgsConstructor
public class PagedResponse<T> {
    private int currentPage;
    private int pageSize;
    private long total;
    private long totalPages;
    private List<T> data;

    @Builder
    public PagedResponse(Integer currentPage,
                         Integer pageSize,
                         Long total,
                         Long totalPages,
                         List<T> data) {
        this.currentPage = requireNonNull(currentPage, "Current page is required");
        this.pageSize = requireNonNull(pageSize, "Page size is required");
        this.total = requireNonNull(total, "Total is required");
        this.totalPages = requireNonNull(totalPages, "Total pages is required");
        this.data = requireNonNull(data, "Data is required");
    }
}
