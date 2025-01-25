package com.task.management.application.model;

import static java.util.Objects.requireNonNull;

public record ProjectId(Long value) {
    public ProjectId {
        requireNonNull(value, "Id value is required");
    }
}
