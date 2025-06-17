package com.task.management.domain.project.model.objectvalue;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public record TaskCommentId(Long value) {
    public TaskCommentId {
        parameterRequired(value, "Task comment id value");
    }
}
