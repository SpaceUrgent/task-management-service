package com.task.managment.web.dashboard.dto;

import lombok.Builder;
import lombok.Data;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@Data
public class TasksSummaryDto {
    private Integer total;
    private Integer open;
    private Integer overdue;
    private Integer closed;

    @Builder
    public TasksSummaryDto(Integer total,
                           Integer open,
                           Integer overdue,
                           Integer closed) {
        this.total = parameterRequired(total, "Total");
        this.open = parameterRequired(open, "Open");
        this.overdue = parameterRequired(overdue, "Overdue");
        this.closed = parameterRequired(closed, "Closed");
    }
}
