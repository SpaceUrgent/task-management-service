package com.task.management.domain.project.model;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record MemberId(Long value) {

    public MemberId {
        parameterRequired(value, "Member id value");
    }
}
