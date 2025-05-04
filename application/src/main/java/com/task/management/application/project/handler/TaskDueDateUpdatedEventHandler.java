package com.task.management.application.project.handler;

import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.domain.project.event.TaskDueDateUpdatedEvent;
import com.task.management.domain.project.model.objectvalue.TaskProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@AppComponent
public class TaskDueDateUpdatedEventHandler extends TaskUpdatedEventHandler<TaskDueDateUpdatedEvent, LocalDate> {

    protected TaskDueDateUpdatedEventHandler(TaskRepositoryPort taskRepositoryPort) {
        super(taskRepositoryPort);
    }

    @Override
    public Class<TaskDueDateUpdatedEvent> eventType() {
        return TaskDueDateUpdatedEvent.class;
    }

    @Override
    protected TaskProperty getTargetProperty() {
        return TaskProperty.DUE_DATE;
    }

    @Override
    protected String mapToString(LocalDate source) {
        return source.format(DateTimeFormatter.ISO_DATE);
    }
}
