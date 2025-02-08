package com.task.management.application.project.model;

import java.util.Objects;

public record TaskId(Long value) {
    public TaskId {
        Objects.requireNonNull(value, "Id value is required");
    }
}
