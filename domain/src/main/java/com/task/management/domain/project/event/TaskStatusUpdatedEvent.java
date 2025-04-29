package com.task.management.domain.project.event;

import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskStatus;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public class TaskStatusUpdatedEvent extends TaskUpdatedEvent<TaskStatus> {

    public TaskStatusUpdatedEvent(TaskId taskId, UserId actorId, TaskStatus initialValue, TaskStatus newValue) {
        super(
                taskId,
                actorId,
                parameterRequired(initialValue, "Initial status"),
                parameterRequired(newValue, "New status")
        );
    }
}
