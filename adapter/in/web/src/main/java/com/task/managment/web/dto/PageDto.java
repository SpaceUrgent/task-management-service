package com.task.managment.web.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class PageDto<T> {
    private final int currentPage;
    private final int pageSize;
    private final List<T> data;

    @Builder
    public PageDto(int currentPage,
                   int pageSize,
                   List<T> data) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.data = data;
    }
}
