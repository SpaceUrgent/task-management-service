package com.task.management.domain.shared.model.objectvalue;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public record ProjectId(Long value) {
    public ProjectId {
        parameterRequired(value, "Id value");
    }
}
