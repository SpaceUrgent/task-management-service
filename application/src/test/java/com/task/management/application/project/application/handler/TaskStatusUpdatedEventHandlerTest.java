package com.task.management.application.project.application.handler;

import com.task.management.application.common.EventHandlingException;
import com.task.management.domain.project.event.TaskStatusUpdatedEvent;
import com.task.management.application.project.handler.TaskStatusUpdatedEventHandler;
import com.task.management.domain.project.model.TaskProperty;
import com.task.management.domain.project.model.TaskChangeLog;
import com.task.management.domain.project.model.TaskStatus;
import com.task.management.application.project.port.out.TaskRepositoryPort;
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
class TaskStatusUpdatedEventHandlerTest {

    @Mock
    private TaskRepositoryPort taskRepositoryPort;
    @InjectMocks
    private TaskStatusUpdatedEventHandler handler;

    @Test
    void handle() throws EventHandlingException {
        final var givenEvent = new TaskStatusUpdatedEvent(
                randomTaskId(),
                randomUserId(),
                TaskStatus.TO_DO,
                TaskStatus.IN_PROGRESS
        );
        final var expectedChangeLog = TaskChangeLog.builder()
                .time(givenEvent.getOccurredAt())
                .taskId(givenEvent.getEntityId())
                .actorId(givenEvent.getActorId())
                .targetProperty(TaskProperty.STATUS)
                .initialValue(givenEvent.getInitialValue().name())
                .newValue(givenEvent.getNewValue().name())
                .build();
        handler.handle(givenEvent);
        Mockito.verify(taskRepositoryPort).save(eq(expectedChangeLog));
    }
}