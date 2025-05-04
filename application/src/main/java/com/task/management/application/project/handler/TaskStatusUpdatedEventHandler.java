package com.task.management.application.project.handler;

import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.domain.project.event.TaskStatusUpdatedEvent;
import com.task.management.domain.project.model.objectvalue.TaskProperty;

@AppComponent
public class TaskStatusUpdatedEventHandler extends TaskUpdatedEventHandler<TaskStatusUpdatedEvent, String> {

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
    protected String mapToString(String source) {
        return source;
    }
}
