package com.task.management.domain.project.event;

import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.TaskId;
import com.task.management.domain.project.model.objectvalue.TaskPriority;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public class TaskPriorityUpdatedEvent extends TaskUpdatedEvent<TaskPriority> {

    public TaskPriorityUpdatedEvent(TaskId taskId,
                                       UserId actorId,
                                       TaskPriority initialValue,
                                       TaskPriority newValue) {
        super(
                taskId,
                actorId,
                parameterRequired(initialValue, "Initial priority"),
                parameterRequired(newValue, "New priority")
        );
    }
}
