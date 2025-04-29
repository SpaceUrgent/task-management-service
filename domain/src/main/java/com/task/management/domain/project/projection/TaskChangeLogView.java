package com.task.management.domain.project.projection;

import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.project.model.TaskProperty;
import lombok.Builder;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record TaskChangeLogView(
        Instant time,
        UserInfo actor,
        TaskProperty targetProperty,
        String initialValue,
        String newValue
) {

    @Builder
    public TaskChangeLogView {
        parameterRequired(time, "Time");
        parameterRequired(actor, "Actor");
        parameterRequired(targetProperty, "Target property");
    }
}
