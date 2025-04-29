package com.task.management.domain.project.event;

import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskDescriptionUpdatedEvent extends TaskUpdatedEvent<String> {

    public TaskDescriptionUpdatedEvent(TaskId taskId, UserId actorId, String initialValue, String newValue) {
        super(taskId, actorId, initialValue, newValue);
    }
}
