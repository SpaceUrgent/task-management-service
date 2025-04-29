package com.task.management.domain.project.application.handler;

import com.task.management.domain.common.event.EventHandlingException;
import com.task.management.domain.common.port.out.UserInfoRepositoryPort;
import com.task.management.domain.project.event.TaskReassignedEvent;
import com.task.management.domain.project.model.TaskProperty;
import com.task.management.domain.project.model.TaskChangeLog;
import com.task.management.domain.project.port.out.TaskRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.task.management.domain.project.ProjectTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskReassignedEventHandlerTest {
    @Mock
    private UserInfoRepositoryPort userInfoRepositoryPort;
    @Mock
    private TaskRepositoryPort taskRepositoryPort;
    @InjectMocks
    private TaskReassignedEventHandler handler;

    @Test
    void handle() throws EventHandlingException {
        final var givenEvent = new TaskReassignedEvent(
                randomTaskId(),
                randomUserId(),
                randomUserId(),
                randomUserId()
        );
        final var initialAssigneeInfo = randomUserInfo();
        final var newAssigneeInfo = randomUserInfo();

        doReturn(Optional.of(initialAssigneeInfo)).when(userInfoRepositoryPort).find(eq(givenEvent.getInitialValue()));
        doReturn(Optional.of(newAssigneeInfo)).when(userInfoRepositoryPort).find(eq(givenEvent.getNewValue()));

        final var expectedChangeLog = TaskChangeLog.builder()
                .time(givenEvent.getOccurredAt())
                .taskId(givenEvent.getEntityId())
                .actorId(givenEvent.getActorId())
                .targetProperty(TaskProperty.ASSIGNEE)
                .initialValue(initialAssigneeInfo.fullName())
                .newValue(newAssigneeInfo.fullName())
                .build();
        handler.handle(givenEvent);
        verify(taskRepositoryPort).save(eq(expectedChangeLog));
    }

    @Test
    void handle_shouldThrow_whenInitialAssigneeNotFound() {
        final var givenEvent = new TaskReassignedEvent(
                randomTaskId(),
                randomUserId(),
                randomUserId(),
                randomUserId()
        );

        doReturn(Optional.empty()).when(userInfoRepositoryPort).find(eq(givenEvent.getInitialValue()));

        assertThrows(
                EventHandlingException.class,
                () -> handler.handle(givenEvent)
        );
        verifyNoInteractions(taskRepositoryPort);
    }

    @Test
    void handle_shouldThrow_whenInitialNewAssigneeNotFound() {
        final var givenEvent = new TaskReassignedEvent(
                randomTaskId(),
                randomUserId(),
                randomUserId(),
                randomUserId()
        );

        doReturn(Optional.of(randomUserInfo())).when(userInfoRepositoryPort).find(eq(givenEvent.getInitialValue()));
        doReturn(Optional.empty()).when(userInfoRepositoryPort).find(eq(givenEvent.getNewValue()));

        assertThrows(
                EventHandlingException.class,
                () -> handler.handle(givenEvent)
        );
        verifyNoInteractions(taskRepositoryPort);
    }
}