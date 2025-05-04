package com.task.management.domain.project.model.objectvalue;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record TaskId(Long value) {
    public TaskId {
        parameterRequired(value, "Id value");
    }
}
