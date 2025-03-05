package com.task.managment.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class PageDTO<T> {
    private final int currentPage;
    private final int pageSize;
    private final List<T> data;

    @Builder
    public PageDTO(int currentPage,
                   int pageSize,
                   List<T> data) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.data = data;
    }
}
