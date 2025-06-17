package com.task.management.application.project.handler;

import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.domain.project.event.TaskPriorityUpdatedEvent;
import com.task.management.domain.shared.model.objectvalue.TaskPriority;
import com.task.management.domain.project.model.objectvalue.TaskProperty;

@AppComponent
public class TaskPriorityUpdatedEventHandler extends TaskUpdatedEventHandler<TaskPriorityUpdatedEvent, TaskPriority> {

    protected TaskPriorityUpdatedEventHandler(TaskRepositoryPort taskRepositoryPort) {
        super(taskRepositoryPort);
    }

    @Override
    protected TaskProperty getTargetProperty() {
        return TaskProperty.PRIORITY;
    }

    @Override
    protected String mapToString(TaskPriority source) {
        return source.name();
    }

    @Override
    public Class<TaskPriorityUpdatedEvent> eventType() {
        return TaskPriorityUpdatedEvent.class;
    }
}
