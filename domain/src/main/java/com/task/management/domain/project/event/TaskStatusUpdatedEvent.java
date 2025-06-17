package com.task.management.domain.project.event;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskId;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public class TaskStatusUpdatedEvent extends TaskUpdatedEvent<String> {

    public TaskStatusUpdatedEvent(TaskId taskId, UserId actorId, String initialValue, String newValue) {
        super(
                taskId,
                actorId,
                parameterRequired(initialValue, "Initial status"),
                parameterRequired(newValue, "New status")
        );
    }
}
