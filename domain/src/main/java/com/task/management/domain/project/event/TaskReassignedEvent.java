package com.task.management.domain.project.event;

import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.common.model.objectvalue.TaskId;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

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
