package com.task.management.application.dashboard.projection;

import lombok.Builder;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public record TasksSummary(
        Integer total,
        Integer open,
        Integer overdue,
        Integer closed
) {

    @Builder
    public TasksSummary {
        parameterRequired(total, "Total");
        parameterRequired(open, "Open");
        parameterRequired(overdue, "Overdue");
        parameterRequired(closed, "Closed");
    }
}
