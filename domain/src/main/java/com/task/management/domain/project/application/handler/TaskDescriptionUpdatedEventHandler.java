package com.task.management.domain.project.application.handler;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.project.event.TaskDescriptionUpdatedEvent;
import com.task.management.domain.project.model.TaskProperty;
import com.task.management.domain.project.port.out.TaskRepositoryPort;

@AppComponent
public class TaskDescriptionUpdatedEventHandler extends TaskUpdatedEventHandler<TaskDescriptionUpdatedEvent, String> {

    protected TaskDescriptionUpdatedEventHandler(TaskRepositoryPort taskRepositoryPort) {
        super(taskRepositoryPort);
    }

    @Override
    public Class<TaskDescriptionUpdatedEvent> eventType() {
        return TaskDescriptionUpdatedEvent.class;
    }

    @Override
    protected TaskProperty getTargetProperty() {
        return TaskProperty.DESCRIPTION;
    }

    @Override
    protected String mapToString(String source) {
        return source;
    }
}
