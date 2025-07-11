package com.task.management.domain.project.model.objectvalue;

import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.UserId;
import lombok.Builder;

import java.time.Instant;

import static com.task.management.domain.shared.validation.Validation.*;

public record TaskChangeLog(
        Instant time,
        TaskId taskId,
        UserId actorId,
        TaskProperty targetProperty,
        String initialValue,
        String newValue
) {

    @Builder
    public TaskChangeLog {
        parameterRequired(time, "Time");
        parameterRequired(taskId, "Task id");
        actorIdRequired(actorId);
        parameterRequired(targetProperty, "Target property");
    }
}
