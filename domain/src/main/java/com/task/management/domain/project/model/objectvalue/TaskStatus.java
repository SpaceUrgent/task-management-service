package com.task.management.domain.project.model.objectvalue;

import com.task.management.domain.shared.validation.ValidationException;
import lombok.Builder;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public record TaskStatus(
        String name,
        Integer position
) {

    @Builder
    public TaskStatus {
        parameterRequired(name, "Task name");
        parameterRequired(position, "Task position");
        if (position < 1) {
            throw new ValidationException("Position can't be negative");
        }
    }

    public TaskStatus changePosition(Integer position) {
        parameterRequired(position, "Task position");
        return new TaskStatus(this.name(), position);
    }
}
