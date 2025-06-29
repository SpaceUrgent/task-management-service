package com.task.management.application.project.handler;

import com.task.management.application.shared.EventHandlingException;
import com.task.management.domain.project.event.TaskTitleUpdatedEvent;
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
class TaskTitleUpdatedEventHandlerTest {

    @Mock
    private TaskRepositoryPort taskRepositoryPort;
    @InjectMocks
    private TaskTitleUpdatedEventHandler handler;

    @Test
    void handle() throws EventHandlingException {
        final var givenEvent = new TaskTitleUpdatedEvent(
                randomTaskId(),
                randomUserId(),
                "Initial title",
                "New title"
        );
        final var expectedChangeLog = TaskChangeLog.builder()
                .time(givenEvent.getOccurredAt())
                .taskId(givenEvent.getTaskId())
                .actorId(givenEvent.getActorId())
                .targetProperty(TaskProperty.TITLE)
                .initialValue(givenEvent.getInitialValue())
                .newValue(givenEvent.getNewValue())
                .build();
        handler.handle(givenEvent);
        Mockito.verify(taskRepositoryPort).save(eq(expectedChangeLog));
    }

}