package com.task.management.domain.iam.model;

import static java.util.Objects.requireNonNull;

public record UserId(Long value) {
    public UserId {
        requireNonNull(value, "User id value is required");
    }
}
