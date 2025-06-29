package com.task.management.application.project.handler;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.domain.project.event.TaskDescriptionUpdatedEvent;
import com.task.management.domain.project.model.objectvalue.TaskProperty;

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
