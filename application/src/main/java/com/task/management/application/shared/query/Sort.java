package com.task.management.application.shared.query;

import lombok.Data;

import static java.util.Objects.requireNonNull;

@Data
public class Sort {
    private final String property;
    private final Direction direction;

    private Sort(String property, Direction direction) {
        this.property = requireNonNull(property, "Property is required");
        this.direction = requireNonNull(direction, "Direction is required");
    }

    public static Sort by(String property, Direction direction) {
        return new Sort(property, direction);
    }

    public enum Direction {
        ASC, DESC
    }
}
