package com.task.management.application.project.handler;

import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.application.shared.EventHandlingException;
import com.task.management.application.shared.port.out.DomainEventPublisherPort;
import com.task.management.domain.project.event.MemberLeftProjectEvent;
import com.task.management.domain.project.event.TaskReassignedEvent;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.shared.event.DomainEvent;
import com.task.management.domain.shared.model.objectvalue.TaskNumber;
import com.task.management.domain.shared.model.objectvalue.TaskPriority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static com.task.management.application.shared.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberLeftProjectEventHandlerTest {
    @Mock
    private TaskRepositoryPort taskRepositoryPort;
    @Mock
    private DomainEventPublisherPort publisherPort;
    @InjectMocks
    private MemberLeftProjectEventHandler eventHandler;

    @Test
    void handle() throws EventHandlingException {
        final var givenEvent = new MemberLeftProjectEvent(randomUserId(), randomProjectId());
        final var existingTask = Task.builder()
                .createdAt(Instant.now())
                .id(randomTaskId())
                .project(givenEvent.getProjectId())
                .title("Task title")
                .description("Task description")
                .status("To do")
                .owner(randomUserId())
                .assignee(givenEvent.getMemberId())
                .number(new TaskNumber(10L))
                .priority(TaskPriority.MEDIUM)
                .build();
        doReturn(Stream.of(existingTask))
                .when(taskRepositoryPort)
                .findAllByAssigneeAndProject(eq(givenEvent.getMemberId()), eq(givenEvent.getProjectId()));

        eventHandler.handle(givenEvent);

        verify(taskRepositoryPort).unassignTasksFrom(eq(givenEvent.getMemberId()), eq(givenEvent.getProjectId()));
        ArgumentCaptor<List<DomainEvent>> eventsCaptor = ArgumentCaptor.captor();
        verify(publisherPort).publish(eventsCaptor.capture());
        final var publishedEvents = eventsCaptor.getValue();
        final var taskReassignedEvent = assertInstanceOf(TaskReassignedEvent.class, publishedEvents.getFirst());
        assertNotNull(taskReassignedEvent.getOccurredAt());
        assertEquals(existingTask.getId(), taskReassignedEvent.getTaskId());
        assertEquals(existingTask.getAssignee(), taskReassignedEvent.getInitialValue());
        assertNull(taskReassignedEvent.getActorId());
        assertNull(taskReassignedEvent.getNewValue());
    }
}