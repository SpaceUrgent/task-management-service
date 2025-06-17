package com.task.management.domain.shared.model.objectvalue;

import com.task.management.domain.shared.validation.Validation;

public record TaskNumber(Long value) {
    public TaskNumber {
        Validation.parameterRequired(value, "Task number value");
    }
}
