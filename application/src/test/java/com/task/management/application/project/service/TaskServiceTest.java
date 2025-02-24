package com.task.management.application.project.service;

import com.task.management.application.common.Page;
import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.ValidationService;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.Task;
import com.task.management.application.project.model.TaskDetails;
import com.task.management.application.project.model.TaskPreview;
import com.task.management.application.project.model.TaskStatus;
import com.task.management.application.project.port.in.command.CreateTaskCommand;
import com.task.management.application.project.port.in.command.UpdateTaskCommand;
import com.task.management.application.project.port.in.query.FindTasksQuery;
import com.task.management.application.project.port.out.FindProjectTasksPort;
import com.task.management.application.project.port.out.FindTaskByIdPort;
import com.task.management.application.project.port.out.FindTaskDetailsByIdPort;
import com.task.management.application.project.port.out.AddTaskPort;
import com.task.management.application.project.port.out.UpdateTaskPort;
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
    private AddTaskPort addTaskPort;
    @Mock
    private UpdateTaskPort updateTaskPort;
    @Mock
    private FindTaskByIdPort findTaskByIdPort;
    @Mock
    private FindTaskDetailsByIdPort findTaskDetailsByIdPort;
    @Mock
    private FindProjectTasksPort findProjectTasksPort;
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
                .when(addTaskPort)
                .add(any());
        final var created = taskService.createTask(givenActorId, givenCommand);
        assertNotNull(created.getCreatedAt());
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
        verifyNoMoreInteractions(addTaskPort);
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
        verifyNoMoreInteractions(addTaskPort);
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
        doAnswer(self(Task.class)).when(updateTaskPort).update(any());
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
        verifyNoMoreInteractions(updateTaskPort);
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
        verifyNoMoreInteractions(updateTaskPort);
    }

    @Test
    void updateStatus_shouldSaveUpdatedTask_whenAllConditionsMet() throws UseCaseException {
        final var task = randomTask();
        final var givenActorId = task.getAssignee().id();
        final var givenTaskId = task.getId();
        final var givenTaskStatus = TaskStatus.DONE;
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);
        doReturn(Optional.of(task)).when(findTaskByIdPort).find(eq(givenTaskId));
        doAnswer(self(Task.class)).when(updateTaskPort).update(taskCaptor.capture());
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
        verifyNoInteractions(updateTaskPort);
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
        verifyNoInteractions(updateTaskPort);
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
        doAnswer(self(Task.class)).when(updateTaskPort).update(taskCaptor.capture());
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
        verifyNoInteractions(updateTaskPort);
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
        verifyNoInteractions(updateTaskPort);
    }

    @Test
    void getTaskDetails_shouldReturnTaskDetails_whenAllConditionsMet() throws UseCaseException {
        final var expected = randomTaskDetails();
        final var givenActorId = randomProjectUserId();
        final var givenTaskId = expected.id();
        doReturn(Optional.of(expected)).when(findTaskDetailsByIdPort).findTaskDetailsById(eq(givenTaskId));
        doReturn(true).when(projectUserService).isMember(eq(givenActorId), eq(expected.projectId()));
        assertEquals(expected, taskService.getTaskDetails(givenActorId, givenTaskId));
    }

    @Test
    void getTaskDetails_shouldThrowEntityNotFoundException_whenTaskNotFound() {
        final var givenActorId = randomProjectUserId();
        final var givenTaskId = randomTaskId();
        doReturn(Optional.empty()).when(findTaskDetailsByIdPort).findTaskDetailsById(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.getTaskDetails(givenActorId, givenTaskId)
        );
    }

    @Test
    void getTaskDetails_shouldThrowIllegalAccessException_whenActorIsNotProjectMember() throws UseCaseException {
        final var expected = randomTaskDetails();
        final var givenActorId = randomProjectUserId();
        final var givenTaskId = expected.id();
        doReturn(Optional.of(expected)).when(findTaskDetailsByIdPort).findTaskDetailsById(eq(givenTaskId));
        doReturn(false).when(projectUserService).isMember(eq(givenActorId), eq(expected.projectId()));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
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
        final var givenActorId = randomProjectUserId();
        final var expected = Page.<TaskPreview>builder()
                .pageNo(givenQuery.getPageNumber())
                .pageSize(givenQuery.getPageSize())
                .total(100)
                .totalPages(5)
                .content(randomTaskPreviews(givenQuery.getPageSize(), givenQuery.getProjectId()))
                .build();
        doReturn(true).when(projectUserService).isMember(eq(givenActorId), eq(givenQuery.getProjectId()));
        doReturn(expected).when(findProjectTasksPort).findProjectTasks(eq(givenQuery));
        assertEquals(expected, taskService.findTasks(givenActorId, givenQuery));
    }

    @Test
    void findTasks_shouldThrowIllegalAccessException_whenActorIsNotProjectMember() throws UseCaseException {
        final var givenQuery = FindTasksQuery.builder()
                .pageNumber(1)
                .pageSize(20)
                .projectId(randomProjectId())
                .build();
        final var givenActorId = randomProjectUserId();
        doReturn(false).when(projectUserService).isMember(eq(givenActorId), eq(givenQuery.getProjectId()));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.findTasks(givenActorId, givenQuery)
        );
    }

    private static Task randomTask() {
        return Task.builder()
                .id(randomTaskId())
                .createdAt(Instant.now())
                .project(randomProjectId())
                .status(TaskStatus.IN_PROGRESS)
                .title("Title")
                .description("Description")
                .owner(randomProjectUser())
                .assignee(randomProjectUser())
                .build();
    }

    private static TaskDetails randomTaskDetails() {
        return TaskDetails.builder()
                .id(randomTaskId())
                .createdAt(Instant.now())
                .projectId(randomProjectId())
                .status(TaskStatus.IN_PROGRESS)
                .title("Title")
                .description("Description")
                .owner(randomProjectUser())
                .assignee(randomProjectUser())
                .build();
    }

    private static List<TaskPreview> randomTaskPreviews(int total, ProjectId projectId) {
         return IntStream.range(0, total)
                 .mapToObj(value -> randomTaskPreview(projectId))
                 .toList();
    }

    private static TaskPreview randomTaskPreview(ProjectId projectId) {
        return TaskPreview.builder()
                .id(randomTaskId())
                .createdAt(Instant.now())
                .title("Title")
                .status(TaskStatus.IN_PROGRESS)
                .assignee(randomProjectUser())
                .build();
    }
}