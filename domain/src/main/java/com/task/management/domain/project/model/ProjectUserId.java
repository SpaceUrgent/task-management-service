package com.task.management.domain.project.model;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record ProjectUserId(Long value) {
    public ProjectUserId {
        parameterRequired(value, "Project id value");
    }
}
