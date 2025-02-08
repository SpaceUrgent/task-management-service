package com.task.management.application.project.model;

import static com.task.management.application.common.Validation.parameterRequired;

public record ProjectUserId(Long value) {
    public ProjectUserId {
        parameterRequired(value, "Project id value");
    }
}
