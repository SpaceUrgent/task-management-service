package com.task.management.application.project.handler;

import com.task.management.application.shared.EventHandlingException;
import com.task.management.application.project.ProjectConstants;
import com.task.management.domain.project.event.TaskStatusUpdatedEvent;
import com.task.management.domain.project.model.objectvalue.TaskProperty;
import com.task.management.domain.project.model.objectvalue.TaskChangeLog;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.task.management.application.shared.TestUtils.randomTaskId;
import static com.task.management.application.shared.TestUtils.randomUserId;
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
                ProjectConstants.DEFAULT_TASK_STATUSES.getFirst().name(),
                ProjectConstants.DEFAULT_TASK_STATUSES.getLast().name()
        );
        final var expectedChangeLog = TaskChangeLog.builder()
                .time(givenEvent.getOccurredAt())
                .taskId(givenEvent.getTaskId())
                .actorId(givenEvent.getActorId())
                .targetProperty(TaskProperty.STATUS)
                .initialValue(givenEvent.getInitialValue())
                .newValue(givenEvent.getNewValue())
                .build();
        handler.handle(givenEvent);
        Mockito.verify(taskRepositoryPort).save(eq(expectedChangeLog));
    }
}