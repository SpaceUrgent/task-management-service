package com.task.management.domain.project.service;

import com.task.management.domain.common.projection.Page;
import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.validation.ValidationService;
import com.task.management.domain.project.application.service.ProjectService;
import com.task.management.domain.project.application.service.TaskService;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.projection.TaskDetails;
import com.task.management.domain.project.projection.TaskPreview;
import com.task.management.domain.project.model.TaskStatus;
import com.task.management.domain.project.application.command.CreateTaskCommand;
import com.task.management.domain.project.application.command.UpdateTaskCommand;
import com.task.management.domain.project.application.query.FindTasksQuery;
import com.task.management.domain.project.port.out.TaskRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.task.management.domain.project.service.ProjectTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @SuppressWarnings("unused")
    @Mock
    private ValidationService validationService;
    @Mock
    private ProjectService projectService;
    @Mock
    private TaskRepositoryPort taskRepositoryPort;
    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldReturnCreatedTask_whenAllConditionsMet() throws UseCaseException {
        final var owner = randomMemberView();
        final var givenActorId = owner.id();
        final var assignee = randomMemberView();
        final var givenProjectId = randomProjectId();
        final var givenCommand = CreateTaskCommand.builder()
                .title("New task title")
                .description("New task description")
                .assigneeId(assignee.id())
                .build();
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);
        doReturn(true).when(projectService).isMember(eq(givenCommand.assigneeId()), eq(givenProjectId));
        doAnswer(self(Task.class)).when(taskRepositoryPort).save(taskCaptor.capture());
        taskService.createTask(givenActorId, givenProjectId, givenCommand);
        final var created = taskCaptor.getValue();
        assertNotNull(created.getCreatedAt());
        assertEquals(givenCommand.title(), created.getTitle());
        assertEquals(givenCommand.description(), created.getDescription());
        assertEquals(givenActorId, created.getOwner());
        assertEquals(givenCommand.assigneeId(), created.getAssignee());
    }

    @Test
    void createTask_shouldThrowIllegalAccessException_whenOwnerIsNotProjectMember() {
        final var assignee = randomMemberView();
        final var givenActorId = randomUserId();
        final var givenProjectId = randomProjectId();
        final var givenCommand = CreateTaskCommand.builder()
                .title("New task title")
                .description("New task description")
                .assigneeId(assignee.id())
                .build();
        doReturn(false)
                .when(projectService)
                .isMember(eq(givenActorId), eq(givenProjectId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.createTask(givenActorId, givenProjectId, givenCommand)
        );
        verifyNoMoreInteractions(taskRepositoryPort);
    }

    @Test
    void createTask_shouldThrowIllegalAccessException_whenAssigneeIsNotProjectMember() {
        final var owner = randomMemberView();
        final var givenActorId = owner.id();
        final var givenProjectId = randomProjectId();
        final var givenCommand = CreateTaskCommand.builder()
                .title("New task title")
                .description("New task description")
                .assigneeId(randomUserId())
                .build();
        doReturn(false).when(projectService).isMember(eq(givenCommand.assigneeId()), eq(givenProjectId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.createTask(givenActorId, givenProjectId, givenCommand)
        );
        verifyNoMoreInteractions(taskRepositoryPort);
    }

    @Test
    void updateTask_shouldReturnUpdatedTask_whenAllConditionsMet() throws UseCaseException {
        final var task = randomTask();
        final var givenActorId = task.getOwner();
        final var givenCommand = UpdateTaskCommand.builder()
                .title("Updated title")
                .description("Updated description")
                .build();
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(task.getId()));
        doAnswer(self(Task.class)).when(taskRepositoryPort).save(taskCaptor.capture());
        taskService.updateTask(givenActorId, task.getId(), givenCommand);
        final var updated = taskCaptor.getValue();
        assertEquals(task.getProject(), updated.getProject());
        assertEquals(givenCommand.title(), updated.getTitle());
        assertEquals(givenCommand.description(), updated.getDescription());
        assertEquals(task.getStatus(), updated.getStatus());
        assertEquals(task.getOwner(), updated.getOwner());
        assertEquals(task.getAssignee(), updated.getAssignee());
    }

    @Test
    void updateTask_shouldThrowEntityNotFoundException_whenTaskDoesNotExist() {
        final var givenTaskId = randomTaskId();
        final var givenCommand = UpdateTaskCommand.builder()
                .title("Updated title")
                .description("Updated description")
                .build();
        doReturn(Optional.empty()).when(taskRepositoryPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.updateTask(randomUserId(), givenTaskId, givenCommand)
        );
        verify(taskRepositoryPort, times(0)).save(any());
    }

    @Test
    void updateTask_shouldThrowIllegalAccessException_whenCurrentUserIsNotTaskOwner() {
        final var task = randomTask();
        final var givenActorId = randomUserId();
        final var givenCommand = UpdateTaskCommand.builder()
                .title("Updated title")
                .description("Updated description")
                .build();
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(task.getId()));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.updateTask(givenActorId, task.getId(), givenCommand)
        );
        verify(taskRepositoryPort, times(0)).save(any());
    }

    @Test
    void updateStatus_shouldSaveUpdatedTask_whenAllConditionsMet() throws UseCaseException {
        final var task = randomTask();
        final var givenActorId = task.getAssignee();
        final var givenTaskId = task.getId();
        final var givenTaskStatus = TaskStatus.DONE;
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(givenTaskId));
        doAnswer(self(Task.class)).when(taskRepositoryPort).save(taskCaptor.capture());
        taskService.updateStatus(givenActorId, givenTaskId, givenTaskStatus);
        final var saved = taskCaptor.getValue();
        assertEquals(task.getId(), saved.getId());
        assertEquals(givenTaskStatus, saved.getStatus());
    }

    @Test
    void updateStatus_shouldThrowEntityNotFoundException_whenTaskNotFound() {
        final var givenActorId = randomUserId();
        final var givenTaskId = randomTaskId();
        final var givenTaskStatus = TaskStatus.DONE;
        doReturn(Optional.empty()).when(taskRepositoryPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.updateStatus(givenActorId, givenTaskId, givenTaskStatus)
        );
        verify(taskRepositoryPort, times(0)).save(any());
    }

    @Test
    void updateStatus_shouldThrowIllegalAccessException_whenUserIsNorOwnerNorAssignee() {
        final var task = randomTask();
        final var givenActorId = randomUserId();
        final var givenTaskId = randomTaskId();
        final var givenTaskStatus = TaskStatus.DONE;
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.updateStatus(givenActorId, givenTaskId, givenTaskStatus)
        );
        verify(taskRepositoryPort, times(0)).save(any());
    }

    @Test
    void assignTask_shouldSaveUpdatedTask_whenAllConditionsMet() throws UseCaseException {
        final var task = randomTask();
        final var assignee = randomMemberView();
        final var givenActorId = task.getAssignee();
        final var givenTaskId = task.getId();
        final var givenAssigneeId = assignee.id();
        doReturn(true).when(projectService).isMember(eq(givenAssigneeId), eq(task.getProject()));
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(givenTaskId));
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);
        doAnswer(self(Task.class)).when(taskRepositoryPort).save(taskCaptor.capture());
        taskService.assignTask(givenActorId, givenTaskId, givenAssigneeId);
        final var saved = taskCaptor.getValue();
        assertEquals(task.getId(), saved.getId());
        assertEquals(givenAssigneeId, saved.getAssignee());
    }


    @Test
    void assignTask_shouldThrowEntityNotFoundException_whenTaskNotFound() {
        final var givenActorId = randomUserId();
        final var givenTaskId = randomTaskId();
        final var givenAssigneeId = randomUserId();
        doReturn(Optional.empty()).when(taskRepositoryPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.assignTask(givenActorId, givenTaskId, givenAssigneeId)
        );
        verify(taskRepositoryPort, times(0)).save(any());
    }

    @Test
    void assignTask_shouldThrowIllegalAccessException_whenActorDoesNotHaveAccess() {
        final var task = randomTask();
        final var givenActorId = randomUserId();
        final var givenTaskId = task.getId();
        final var givenAssigneeId = randomUserId();
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.assignTask(givenActorId, givenTaskId, givenAssigneeId)
        );
        verify(taskRepositoryPort, times(0)).save(any());
    }

    @Test
    void getTaskDetails_shouldReturnTaskDetails_whenAllConditionsMet() throws UseCaseException {
        final var expected = randomTaskDetails();
        final var givenActorId = randomUserId();
        final var givenTaskId = expected.id();
        doReturn(Optional.of(expected)).when(taskRepositoryPort).findTaskDetails(eq(givenTaskId));
        assertEquals(expected, taskService.getTaskDetails(givenActorId, givenTaskId));
    }

    @Test
    void getTaskDetails_shouldThrowEntityNotFoundException_whenTaskNotFound() {
        final var givenActorId = randomUserId();
        final var givenTaskId = randomTaskId();
        doReturn(Optional.empty()).when(taskRepositoryPort).findTaskDetails(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.getTaskDetails(givenActorId, givenTaskId)
        );
    }

    @Test
    void findTasks_shouldReturnTaskPreviewPage_whenAllConditionsMet() throws UseCaseException {
        final var givenQuery = FindTasksQuery.builder()
                .pageNumber(1)
                .pageSize(20)
                .projectId(randomProjectId())
                .build();
        final var givenActorId = randomUserId();
        final var expected = Page.<TaskPreview>builder()
                .pageNo(givenQuery.getPageNumber())
                .pageSize(givenQuery.getPageSize())
                .total(100)
                .totalPages(5)
                .content(randomTaskPreviews(givenQuery.getPageSize()))
                .build();
        doReturn(expected).when(taskRepositoryPort).findProjectTasks(eq(givenQuery));
        assertEquals(expected, taskService.findTasks(givenActorId, givenQuery));
    }

    private static Task randomTask() {
        return Task.builder()
                .id(randomTaskId())
                .number(randomTaskNumber())
                .createdAt(Instant.now())
                .project(randomProjectId())
                .status(TaskStatus.IN_PROGRESS)
                .title("Title")
                .description("Description")
                .owner(randomUserId())
                .assignee(randomUserId())
                .build();
    }

    private static TaskDetails randomTaskDetails() {
        return TaskDetails.builder()
                .id(randomTaskId())
                .number(randomTaskNumber())
                .createdAt(Instant.now())
                .projectId(randomProjectId())
                .status(TaskStatus.IN_PROGRESS)
                .title("Title")
                .description("Description")
                .owner(randomMemberView())
                .assignee(randomMemberView())
                .build();
    }

    private static List<TaskPreview> randomTaskPreviews(int total) {
         return IntStream.range(0, total)
                 .mapToObj(value -> randomTaskPreview())
                 .toList();
    }

    private static TaskPreview randomTaskPreview() {
        return TaskPreview.builder()
                .id(randomTaskId())
                .number(randomTaskNumber())
                .createdAt(Instant.now())
                .title("Title")
                .status(TaskStatus.IN_PROGRESS)
                .assignee(randomMemberView())
                .build();
    }
}