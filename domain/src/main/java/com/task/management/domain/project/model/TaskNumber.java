package com.task.management.domain.project.model;

import com.task.management.domain.common.validation.Validation;

public record TaskNumber(Long value) {
    public TaskNumber {
        Validation.parameterRequired(value, "Task number value");
    }
}
