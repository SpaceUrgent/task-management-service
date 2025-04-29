package com.task.management.domain.project.application.handler;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.project.event.TaskStatusUpdatedEvent;
import com.task.management.domain.project.model.TaskProperty;
import com.task.management.domain.project.model.TaskStatus;
import com.task.management.domain.project.port.out.TaskRepositoryPort;

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
