package com.task.management.domain.project.model.objectvalue;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record ProjectId(Long value) {
    public ProjectId {
        parameterRequired(value, "Id value");
    }
}
