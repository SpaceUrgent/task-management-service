package com.task.management.application.project.handler;

import com.task.management.application.common.EventHandlingException;
import com.task.management.domain.project.event.TaskDescriptionUpdatedEvent;
import com.task.management.application.project.handler.TaskDescriptionUpdatedEventHandler;
import com.task.management.domain.project.model.objectvalue.TaskProperty;
import com.task.management.domain.project.model.objectvalue.TaskChangeLog;
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
class TaskDescriptionUpdatedEventHandlerTest {

    @Mock
    private TaskRepositoryPort taskRepositoryPort;
    @InjectMocks
    private TaskDescriptionUpdatedEventHandler handler;

    @Test
    void handle() throws EventHandlingException {
        final var givenEvent = new TaskDescriptionUpdatedEvent(
                randomTaskId(),
                randomUserId(),
                "Initial Description",
                "New Description"
        );
        final var expectedChangeLog = TaskChangeLog.builder()
                .time(givenEvent.getOccurredAt())
                .taskId(givenEvent.getEntityId())
                .actorId(givenEvent.getActorId())
                .targetProperty(TaskProperty.DESCRIPTION)
                .initialValue(givenEvent.getInitialValue())
                .newValue(givenEvent.getNewValue())
                .build();
        handler.handle(givenEvent);
        Mockito.verify(taskRepositoryPort).save(eq(expectedChangeLog));
    }
}