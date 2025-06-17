package com.task.management.domain.project.event;

import com.task.management.domain.shared.event.AbstractDomainEvent;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public abstract class TaskUpdatedEvent<T> extends AbstractDomainEvent<TaskId> {
    private final T initialValue;
    private final T newValue;

    protected TaskUpdatedEvent(TaskId taskId,
                               UserId actorId,
                               T initialValue,
                               T newValue) {
        super(taskId, actorId);
        this.initialValue = initialValue;
        this.newValue = newValue;
    }
}
