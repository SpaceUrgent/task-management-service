package com.task.management.domain.iam.model;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record UserId(Long value) {
    public UserId {
        parameterRequired(value, "User id value");
    }
}
