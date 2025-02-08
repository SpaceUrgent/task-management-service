package com.task.management.application.project.model;

import static java.util.Objects.requireNonNull;

public record ProjectId(Long value) {
    public ProjectId {
        requireNonNull(value, "Id value is required");
    }
}
