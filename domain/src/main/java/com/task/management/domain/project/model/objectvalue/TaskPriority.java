package com.task.management.domain.project.model.objectvalue;

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
}
