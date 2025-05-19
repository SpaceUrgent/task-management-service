package com.task.management.domain.project.model.objectvalue;

import com.task.management.domain.common.validation.ValidationException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public enum TaskPriority {
    LOWEST("Lowest", 0),
    LOW("Low",1),
    MEDIUM("Medium",2),
    HIGH("High", 3),
    HIGHEST("Highest", 4);

    private final String priorityName;
    private final Integer order;

    TaskPriority(String priorityName, Integer order) {
        this.priorityName = priorityName;
        this.order = order;
    }

    public String priorityName() {
        return this.priorityName;
    }

    public Integer order() {
        return order;
    }

    public static TaskPriority withPriorityName(String priorityName) {
        parameterRequired(priorityName, "Task priority name");
        return Arrays.stream(TaskPriority.values())
                .filter(priority -> priorityName.equals(priority.priorityName()))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Unknown task priority name value - %s".formatted(priorityName)));
    }

    public static TaskPriority withOrder(Integer order) {
        parameterRequired(order, "Task priority order");
        return Arrays.stream(TaskPriority.values())
                .filter(priority -> order.equals(priority.order))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Unknown task priority order value - %d".formatted(order)));
    }

    public static List<TaskPriority> orderedList() {
        return Arrays.stream(TaskPriority.values())
                .sorted(Comparator.comparing(TaskPriority::order))
                .toList();
    }
}
