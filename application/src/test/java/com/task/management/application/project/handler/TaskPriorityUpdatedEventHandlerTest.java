package com.task.management.application.project.handler;

import com.task.management.application.common.EventHandlingException;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.domain.project.event.TaskPriorityUpdatedEvent;
import com.task.management.domain.project.model.objectvalue.TaskChangeLog;
import com.task.management.domain.project.model.objectvalue.TaskPriority;
import com.task.management.domain.project.model.objectvalue.TaskProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.task.management.application.project.ProjectTestUtils.randomTaskId;
import static com.task.management.application.project.ProjectTestUtils.randomUserId;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class TaskPriorityUpdatedEventHandlerTest {
    @Mock
    private TaskRepositoryPort taskRepositoryPort;
    @InjectMocks
    private TaskPriorityUpdatedEventHandler handler;

    @Test
    void handle() throws EventHandlingException {
        final var givenEvent = new TaskPriorityUpdatedEvent(
                randomTaskId(),
                randomUserId(),
                TaskPriority.LOWEST,
                TaskPriority.HIGHEST
        );
        final var expectedChangeLog = TaskChangeLog.builder()
                .time(givenEvent.getOccurredAt())
                .taskId(givenEvent.getEntityId())
                .actorId(givenEvent.getActorId())
                .targetProperty(TaskProperty.PRIORITY)
                .initialValue(givenEvent.getInitialValue().name())
                .newValue(givenEvent.getNewValue().name())
                .build();
        handler.handle(givenEvent);
        Mockito.verify(taskRepositoryPort).save(eq(expectedChangeLog));
    }
}