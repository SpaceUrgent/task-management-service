package com.task.management.persistence.jpa.view;

import lombok.Builder;

public record TasksSummaryView(
        Integer total,
        Integer open,
        Integer overdue,
        Integer closed
) {

    @Builder
    public TasksSummaryView {
    }
}
