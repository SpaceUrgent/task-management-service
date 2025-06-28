package com.task.management.domain.project.event;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskId;

public class TaskReassignedEvent extends TaskUpdatedEvent<UserId> {

    public TaskReassignedEvent(TaskId taskId, UserId actorId, UserId initialValue, UserId newValue) {
        super(
                taskId,
                actorId,
                initialValue,
                newValue
        );
    }
}
