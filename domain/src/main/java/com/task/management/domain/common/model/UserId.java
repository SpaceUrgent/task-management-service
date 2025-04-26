package com.task.management.domain.common.model;

import java.io.Serializable;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record UserId(Long value) implements Serializable {
    public UserId {
        parameterRequired(value, "User id value");
    }
}
