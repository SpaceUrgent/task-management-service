package com.task.management.domain.project.model.objectvalue;

import com.task.management.domain.common.validation.ValidationException;

import java.util.Arrays;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public enum TaskPriority {
    LOWEST(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    HIGHEST(4);

    private final Integer order;

    TaskPriority(Integer order) {
        this.order = order;
    }

    public Integer order() {
        return order;
    }

    public static TaskPriority withOrder(Integer order) {
        parameterRequired(order, "Task priority order");
        return Arrays.stream(TaskPriority.values())
                .filter(priority -> order.equals(priority.order))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Unknown task priority order value - %d".formatted(order)));
    }
}
