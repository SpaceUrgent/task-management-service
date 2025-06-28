package com.task.management.application.project.handler;

import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.application.shared.EventHandlingException;
import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.shared.event.DomainEventHandler;
import com.task.management.application.shared.port.out.DomainEventPublisherPort;
import com.task.management.domain.project.event.MemberLeftProjectEvent;
import com.task.management.domain.project.event.TaskReassignedEvent;
import com.task.management.domain.shared.model.DomainEventAggregate;

import java.util.List;

import static com.task.management.domain.shared.validation.Validation.eventRequired;

@AppComponent
public class MemberLeftProjectEventHandler implements DomainEventHandler<MemberLeftProjectEvent> {
    private final TaskRepositoryPort taskRepositoryPort;
    private final DomainEventPublisherPort publisherPort;

    public MemberLeftProjectEventHandler(TaskRepositoryPort taskRepositoryPort,
                                         DomainEventPublisherPort publisherPort) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.publisherPort = publisherPort;
    }

    @Override
    public void handle(MemberLeftProjectEvent event) throws EventHandlingException {
        eventRequired(event);
        final var memberId = event.getMemberId();
        final var projectId = event.getProjectId();
        final var events = taskRepositoryPort.findAllByAssigneeAndProject(memberId, projectId)
                .peek(task -> new TaskReassignedEvent(task.getId(), null, task.getAssignee(), null))
                .map(DomainEventAggregate::flushEvents)
                .flatMap(List::stream)
                .toList();
        publisherPort.publish(events);
        taskRepositoryPort.unassignTasksFrom(memberId, projectId);
    }

    @Override
    public Class<MemberLeftProjectEvent> eventType() {
        return MemberLeftProjectEvent.class;
    }
}
