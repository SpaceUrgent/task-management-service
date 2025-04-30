package com.task.management.application.project.handler;

import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.domain.project.event.TaskStatusUpdatedEvent;
import com.task.management.domain.project.model.TaskProperty;
import com.task.management.domain.project.model.TaskStatus;

@AppComponent
public class TaskStatusUpdatedEventHandler extends TaskUpdatedEventHandler<TaskStatusUpdatedEvent, TaskStatus> {

    protected TaskStatusUpdatedEventHandler(TaskRepositoryPort taskRepositoryPort) {
        super(taskRepositoryPort);
    }

    @Override
    public Class<TaskStatusUpdatedEvent> eventType() {
        return TaskStatusUpdatedEvent.class;
    }

    @Override
    protected TaskProperty getTargetProperty() {
        return TaskProperty.STATUS;
    }

    @Override
    protected String mapToString(TaskStatus source) {
        return source.name();
    }
}
