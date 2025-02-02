package com.task.management.application.model;

import java.util.Objects;

public record UserId(Long value) {
    public UserId {
        Objects.requireNonNull(value, "Id value is required");
    }
}
