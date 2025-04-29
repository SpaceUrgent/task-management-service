package com.task.management.domain.project.application.handler;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.project.event.TaskTitleUpdatedEvent;
import com.task.management.domain.project.model.TaskProperty;
import com.task.management.domain.project.port.out.TaskRepositoryPort;

@AppComponent
public class TaskTitleUpdatedEventHandler extends TaskUpdatedEventHandler<TaskTitleUpdatedEvent, String> {

    protected TaskTitleUpdatedEventHandler(TaskRepositoryPort taskRepositoryPort) {
        super(taskRepositoryPort);
    }

    @Override
    public Class<TaskTitleUpdatedEvent> eventType() {
        return TaskTitleUpdatedEvent.class;
    }

    @Override
    protected TaskProperty getTargetProperty() {
        return TaskProperty.TITLE;
    }

    @Override
    protected String mapToString(String source) {
        return source;
    }
}
