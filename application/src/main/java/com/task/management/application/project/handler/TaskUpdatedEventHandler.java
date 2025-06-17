package com.task.management.application.project.handler;

import com.task.management.application.common.EventHandlingException;
import com.task.management.application.common.port.in.DomainEventHandlerPort;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.domain.project.event.TaskUpdatedEvent;
import com.task.management.domain.project.model.objectvalue.TaskChangeLog;
import com.task.management.domain.project.model.objectvalue.TaskProperty;

import static com.task.management.domain.shared.validation.Validation.eventRequired;

public abstract class TaskUpdatedEventHandler<Event extends TaskUpdatedEvent<PropType>, PropType> implements DomainEventHandlerPort<Event> {
    protected final TaskRepositoryPort taskRepositoryPort;

    protected TaskUpdatedEventHandler(TaskRepositoryPort taskRepositoryPort) {
        this.taskRepositoryPort = taskRepositoryPort;
    }

    @Override
    public void handle(Event event) throws EventHandlingException {
        eventRequired(event);
        final var changeLog = TaskChangeLog.builder()
                .time(event.getOccurredAt())
                .taskId(event.getEntityId())
                .actorId(event.getActorId())
                .targetProperty(getTargetProperty())
                .initialValue(getInitialValue(event))
                .newValue(getNewValue(event))
                .build();
        taskRepositoryPort.save(changeLog);
    }

    protected abstract TaskProperty getTargetProperty();

    protected String getInitialValue(Event event) throws EventHandlingException {
        return mapToString(event.getInitialValue());
    }

    protected String getNewValue(Event event) throws EventHandlingException {
        return mapToString(event.getNewValue());
    }

    protected abstract String mapToString(PropType source) throws EventHandlingException;
}
