package com.task.management.domain.shared.model.objectvalue;

import java.io.Serializable;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public record UserId(Long value) implements Serializable {
    public UserId {
        parameterRequired(value, "User id value");
    }
}
