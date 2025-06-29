package com.task.management.application.project.handler;

import com.task.management.application.shared.EventHandlingException;
import com.task.management.domain.project.event.TaskDueDateUpdatedEvent;
import com.task.management.domain.project.model.objectvalue.TaskProperty;
import com.task.management.domain.project.model.objectvalue.TaskChangeLog;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.task.management.application.shared.TestUtils.randomTaskId;
import static com.task.management.application.shared.TestUtils.randomUserId;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class TaskDueDateUpdatedEventHandlerTest {

    @Mock
    private TaskRepositoryPort taskRepositoryPort;
    @InjectMocks
    private TaskDueDateUpdatedEventHandler handler;

    @Test
    void handle() throws EventHandlingException {
        final var givenEvent = new TaskDueDateUpdatedEvent(
                randomTaskId(),
                randomUserId(),
                LocalDate.now().minusWeeks(1),
                LocalDate.now().plusMonths(1)
        );
        final var expectedChangeLog = TaskChangeLog.builder()
                .time(givenEvent.getOccurredAt())
                .taskId(givenEvent.getTaskId())
                .actorId(givenEvent.getActorId())
                .targetProperty(TaskProperty.DUE_DATE)
                .initialValue(givenEvent.getInitialValue().format(DateTimeFormatter.ISO_DATE))
                .newValue(givenEvent.getNewValue().format(DateTimeFormatter.ISO_DATE))
                .build();
        handler.handle(givenEvent);
        Mockito.verify(taskRepositoryPort).save(eq(expectedChangeLog));
    }
}