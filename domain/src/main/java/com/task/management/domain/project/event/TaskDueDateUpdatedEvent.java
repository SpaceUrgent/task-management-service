package com.task.management.domain.project.event;

import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskDueDateUpdatedEvent extends TaskUpdatedEvent<LocalDate> {

    public TaskDueDateUpdatedEvent(TaskId taskId, UserId actorId, LocalDate initialValue, LocalDate newValue) {
        super(taskId, actorId, initialValue, newValue);
    }
}
