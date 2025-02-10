package com.task.management.application.project.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.ValidationService;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.Task;
import com.task.management.application.project.model.TaskStatus;
import com.task.management.application.project.port.in.command.CreateTaskCommand;
import com.task.management.application.project.port.in.command.UpdateTaskCommand;
import com.task.management.application.project.port.out.FindTaskByIdPort;
import com.task.management.application.project.port.out.SaveTaskPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import java.util.Optional;

import static com.task.management.application.project.service.ProjectTestUtils.randomProjectId;
import static com.task.management.application.project.service.ProjectTestUtils.randomProjectUser;
import static com.task.management.application.project.service.ProjectTestUtils.randomProjectUserId;
import static com.task.management.application.project.service.ProjectTestUtils.randomTaskId;
import static com.task.management.application.project.service.ProjectTestUtils.self;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @SuppressWarnings("unused")
    @Mock
    private ValidationService validationService;
    @Mock
    private ProjectUserService projectUserService;
    @Mock
    private SaveTaskPort saveTaskPort;
    @Mock
    private FindTaskByIdPort findTaskByIdPort;
    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldReturnCreatedTask_whenAllConditionsMet() throws UseCaseException {
        final var owner = randomProjectUser();
        final var givenActorId = owner.id();
        final var assignee = randomProjectUser();
        final var givenCommand = CreateTaskCommand.builder()
                .projectId(randomProjectId())
                .title("New task title")
                .description("New task description")
                .assigneeId(assignee.id())
                .build();
        doReturn(Optional.of(owner))
                .when(projectUserService)
                .findProjectMember(eq(givenActorId), eq(givenCommand.projectId()));
        doReturn(Optional.of(assignee))
                .when(projectUserService)
                .findProjectMember(eq(givenCommand.assigneeId()), eq(givenCommand.projectId()));
        doAnswer(self(Task.class))
                .when(saveTaskPort)
                .save(any());
        final var created = taskService.createTask(givenActorId, givenCommand);
        assertEquals(givenCommand.title(), created.getTitle());
        assertEquals(givenCommand.description(), created.getDescription());
        assertEquals(givenActorId, created.getOwner().id());
        assertEquals(givenCommand.assigneeId(), created.getAssignee().id());
    }

    @Test
    void createTask_shouldThrowIllegalAccessException_whenOwnerIsNot() {
        final var assignee = randomProjectUser();
        final var givenActorId = randomProjectUserId();
        final var givenCommand = CreateTaskCommand.builder()
                .projectId(randomProjectId())
                .title("New task title")
                .description("New task description")
                .assigneeId(assignee.id())
                .build();
        doReturn(Optional.empty())
                .when(projectUserService)
                .findProjectMember(eq(givenActorId), eq(givenCommand.projectId()));
        doReturn(Optional.of(assignee))
                .when(projectUserService)
                .findProjectMember(eq(givenCommand.assigneeId()), eq(givenCommand.projectId()));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.createTask(givenActorId, givenCommand)
        );
        verifyNoMoreInteractions(saveTaskPort);
    }

    @Test
    void createTask_shouldThrowIllegalAccessException_whenAllConditionsMet() {
        final var owner = randomProjectUser();
        final var givenActorId = owner.id();
        final var givenCommand = CreateTaskCommand.builder()
                .projectId(randomProjectId())
                .title("New task title")
                .description("New task description")
                .assigneeId(randomProjectUserId())
                .build();
        doReturn(Optional.of(owner))
                .when(projectUserService)
                .findProjectMember(eq(givenActorId), eq(givenCommand.projectId()));
        doReturn(Optional.empty())
                .when(projectUserService)
                .findProjectMember(eq(givenCommand.assigneeId()), eq(givenCommand.projectId()));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.createTask(givenActorId, givenCommand)
        );
        verifyNoMoreInteractions(saveTaskPort);
    }

    @Test
    void updateTask_shouldReturnUpdatedTask_whenAllConditionsMet() throws UseCaseException {
        final var task = randomTask();
        final var givenActorId = task.getOwner().id();
        final var givenCommand = UpdateTaskCommand.builder()
                .taskId(task.getId())
                .title("Updated title")
                .description("Updated description")
                .build();
        doReturn(Optional.of(task)).when(findTaskByIdPort).find(eq(givenCommand.taskId()));
        doAnswer(self(Task.class)).when(saveTaskPort).save(any());
        final var updated = taskService.updateTask(givenActorId, givenCommand);
        assertEquals(task.getProject(), updated.getProject());
        assertEquals(givenCommand.title(), updated.getTitle());
        assertEquals(givenCommand.description(), updated.getDescription());
        assertEquals(task.getStatus(), updated.getStatus());
        assertEquals(task.getOwner(), updated.getOwner());
        assertEquals(task.getAssignee(), updated.getAssignee());
    }

    @Test
    void updateTask_shouldThrowEntityNotFoundException_whenTaskDoesNotExist() {
        final var givenCommand = UpdateTaskCommand.builder()
                .taskId(randomTaskId())
                .title("Updated title")
                .description("Updated description")
                .build();
        doReturn(Optional.empty()).when(findTaskByIdPort).find(eq(givenCommand.taskId()));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.updateTask(randomProjectUserId(), givenCommand)
        );
        verifyNoMoreInteractions(saveTaskPort);
    }

    @Test
    void updateTask_shouldThrowIllegalAccessException_whenCurrentUserIsNotTaskOwner() {
        final var task = randomTask();
        final var givenActorId = randomProjectUserId();
        final var givenCommand = UpdateTaskCommand.builder()
                .taskId(task.getId())
                .title("Updated title")
                .description("Updated description")
                .build();
        doReturn(Optional.of(task)).when(findTaskByIdPort).find(eq(givenCommand.taskId()));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.updateTask(givenActorId, givenCommand)
        );
        verifyNoMoreInteractions(saveTaskPort);
    }

    @Test
    void updateStatus_shouldSaveUpdatedTask_whenAllConditionsMet() throws UseCaseException {
        final var task = randomTask();
        final var givenActorId = task.getAssignee().id();
        final var givenTaskId = task.getId();
        final var givenTaskStatus = TaskStatus.DONE;
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);
        doReturn(Optional.of(task)).when(findTaskByIdPort).find(eq(givenTaskId));
        doAnswer(self(Task.class)).when(saveTaskPort).save(taskCaptor.capture());
        taskService.updateStatus(givenActorId, givenTaskId, givenTaskStatus);
        final var saved = taskCaptor.getValue();
        assertEquals(task.getId(), saved.getId());
        assertEquals(givenTaskStatus, saved.getStatus());
    }

    @Test
    void updateStatus_shouldThrowEntityNotFoundException_whenTaskNotFound() {
        final var givenActorId = randomProjectUserId();
        final var givenTaskId = randomTaskId();
        final var givenTaskStatus = TaskStatus.DONE;
        doReturn(Optional.empty()).when(findTaskByIdPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.updateStatus(givenActorId, givenTaskId, givenTaskStatus)
        );
        verifyNoInteractions(saveTaskPort);
    }

    @Test
    void updateStatus_shouldThrowIllegalAccessException_whenUserIsNorOwnerNorAssignee() {
        final var task = randomTask();
        final var givenActorId = randomProjectUserId();
        final var givenTaskId = randomTaskId();
        final var givenTaskStatus = TaskStatus.DONE;
        doReturn(Optional.of(task)).when(findTaskByIdPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.updateStatus(givenActorId, givenTaskId, givenTaskStatus)
        );
        verifyNoInteractions(saveTaskPort);
    }

    @Test
    void assignTask_shouldSaveUpdatedTask_whenAllConditionsMet() throws UseCaseException {
        final var task = randomTask();
        final var assignee = randomProjectUser();
        final var givenActorId = task.getAssignee().id();
        final var givenTaskId = task.getId();
        final var givenAssigneeId = assignee.id();
        doReturn(Optional.of(task)).when(findTaskByIdPort).find(eq(givenTaskId));
        doReturn(Optional.of(assignee)).when(projectUserService).findProjectMember(eq(givenAssigneeId), eq(task.getProject()));
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);
        doAnswer(self(Task.class)).when(saveTaskPort).save(taskCaptor.capture());
        taskService.assignTask(givenActorId, givenTaskId, givenAssigneeId);
        final var saved = taskCaptor.getValue();
        assertEquals(task.getId(), saved.getId());
        assertEquals(givenAssigneeId, saved.getAssignee().id());
    }

    @Test
    void assignTask_shouldThrowEntityNotFoundException_whenTaskNotFound() {
        final var givenActorId = randomProjectUserId();
        final var givenTaskId = randomTaskId();
        final var givenAssigneeId = randomProjectUserId();
        doReturn(Optional.empty()).when(findTaskByIdPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.assignTask(givenActorId, givenTaskId, givenAssigneeId)
        );
        verifyNoInteractions(saveTaskPort);
    }

    @Test
    void assignTask_shouldThrowIllegalAccessException_whenActorDoesNotHaveAccess() throws UseCaseException {
        final var task = randomTask();
        final var givenActorId = randomProjectUserId();
        final var givenTaskId = task.getId();
        final var givenAssigneeId = randomProjectUserId();
        doReturn(Optional.of(task)).when(findTaskByIdPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.assignTask(givenActorId, givenTaskId, givenAssigneeId)
        );
        verifyNoInteractions(saveTaskPort);
    }

    private static Task randomTask() {
        return Task.builder()
                .id(randomTaskId())
                .project(randomProjectId())
                .status(TaskStatus.IN_PROGRESS)
                .title("Title")
                .description("Description")
                .owner(randomProjectUser())
                .assignee(randomProjectUser())
                .build();
    }
}