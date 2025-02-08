package com.task.management.application.iam.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public record UserId(Long value) {
    public UserId {
        requireNonNull(value, "User id value is required");
    }
}
