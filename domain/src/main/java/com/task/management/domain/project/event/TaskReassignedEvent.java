package com.task.management.domain.project.event;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskId;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public class TaskReassignedEvent extends TaskUpdatedEvent<UserId> {

    public TaskReassignedEvent(TaskId taskId, UserId actorId, UserId initialValue, UserId newValue) {
        super(
                taskId,
                actorId,
                parameterRequired(initialValue, "Initial assignee id"),
                parameterRequired(newValue, "New assignee id")
        );
    }
}
