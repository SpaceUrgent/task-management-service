package com.task.management.application.project.projection;

import com.task.management.domain.shared.model.UserInfo;
import com.task.management.domain.project.model.objectvalue.TaskProperty;
import lombok.Builder;

import java.time.Instant;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

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
        parameterRequired(targetProperty, "Target property");
    }
}
