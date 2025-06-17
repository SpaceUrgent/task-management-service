package com.task.management.domain.project.event;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.TaskPriority;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

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
