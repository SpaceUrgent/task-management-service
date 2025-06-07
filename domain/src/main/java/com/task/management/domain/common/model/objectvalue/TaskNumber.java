package com.task.management.domain.common.model.objectvalue;

import com.task.management.domain.common.validation.Validation;

public record TaskNumber(Long value) {
    public TaskNumber {
        Validation.parameterRequired(value, "Task number value");
    }
}
