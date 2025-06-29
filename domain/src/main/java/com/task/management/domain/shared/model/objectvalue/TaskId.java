package com.task.management.domain.shared.model.objectvalue;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public record TaskId(Long value) {
    public TaskId {
        parameterRequired(value, "Id value");
    }
}
