package com.task.management.domain.project.event;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import lombok.*;

import static com.task.management.domain.shared.validation.Validation.notBlank;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TaskTitleUpdatedEvent extends TaskUpdatedEvent<String> {

    public TaskTitleUpdatedEvent(TaskId taskId,
                                    UserId actorId,
                                    String initialValue,
                                    String newValue) {
        super(
                taskId,
                actorId,
                notBlank(initialValue, "Initial title"),
                notBlank(newValue, "New title")
        );
    }
}
