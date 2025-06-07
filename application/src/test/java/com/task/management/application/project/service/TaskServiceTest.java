package com.task.management.application.project.service;

import com.task.management.application.common.TestUtils;
import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.port.out.TaskCommentRepositoryPort;
import com.task.management.domain.common.event.DomainEvent;
import com.task.management.application.common.port.out.DomainEventPublisherPort;
import com.task.management.application.common.projection.Page;
import com.task.management.application.common.validation.ValidationService;
import com.task.management.application.project.command.CreateTaskCommand;
import com.task.management.application.project.command.UpdateTaskCommand;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.common.model.objectvalue.TaskPriority;
import com.task.management.domain.project.model.objectvalue.TaskProperty;
import com.task.management.application.project.projection.TaskChangeLogView;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.application.project.projection.TaskPreview;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.application.project.query.FindTasksQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.task.management.application.project.ProjectConstants.DEFAULT_TASK_STATUSES;
import static com.task.management.application.project.ProjectTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @SuppressWarnings("unused")
    @Mock
    private ValidationService validationService;
    @Mock
    private DomainEventPublisherPort eventPublisher;
    @Mock
    private ProjectService projectService;
    @Mock
    private TaskRepositoryPort taskRepositoryPort;
    @Mock
    private TaskCommentRepositoryPort taskCommentRepositoryPort;
    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldReturnCreatedTask_whenAllConditionsMet() throws UseCaseException {
        final var owner = randomMemberView();
        final var givenActorId = owner.id();
        final var assignee = randomMemberView();
        final var givenProjectId = TestUtils.randomProjectId();
        final var givenCommand = CreateTaskCommand.builder()
                .title("New task title")
                .description("New task description")
                .assigneeId(assignee.id())
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusMonths(1))
                .build();
        final var taskCaptor = ArgumentCaptor.forClass(Task.class);
        doReturn(true).when(projectService).isMember(eq(givenCommand.assigneeId()), eq(givenProjectId));
        doReturn(DEFAULT_TASK_STATUSES.getFirst()).when(projectService).getInitialTaskStatus(eq(givenProjectId));
        doAnswer(self(Task.class)).when(taskRepositoryPort).save(taskCaptor.capture());
        taskService.createTask(givenActorId, givenProjectId, givenCommand);
        final var created = taskCaptor.getValue();
        assertNotNull(created.getCreatedAt());
        assertEquals(givenCommand.title(), created.getTitle());
        assertEquals(givenCommand.description(), created.getDescription());
        assertEquals(givenActorId, created.getOwner());
        assertEquals(givenCommand.assigneeId(), created.getAssignee());
        assertEquals(givenCommand.priority(), created.getPriority());
        assertEquals(givenCommand.dueDate(), created.getDueDate());
        assertEquals(DEFAULT_TASK_STATUSES.getFirst().name(), created.getStatus());
    }

    @Test
    void createTask_shouldThrowIllegalAccessException_whenOwnerIsNotProjectMember() {
        final var assignee = randomMemberView();
        final var givenActorId = TestUtils.randomUserId();
        final var givenProjectId = TestUtils.randomProjectId();
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
        final var givenProjectId = TestUtils.randomProjectId();
        final var givenCommand = CreateTaskCommand.builder()
                .title("New task title")
                .description("New task description")
                .assigneeId(TestUtils.randomUserId())
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
                .dueDate(LocalDate.now().plusWeeks(1))
                .assigneeId(TestUtils.randomUserId())
                .taskStatus("Done")
                .priority(TaskPriority.HIGHEST)
                .build();

        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(task.getId()));
        doReturn(DEFAULT_TASK_STATUSES).when(projectService).getAvailableTaskStatuses(eq(task.getProject()));
        ArgumentCaptor<List<DomainEvent>> eventsCaptor = ArgumentCaptor.captor();
        taskService.updateTask(givenActorId, task.getId(), givenCommand);

        assertEquals(givenCommand.title(), task.getTitle());
        assertEquals(givenCommand.description(), task.getDescription());
        assertEquals(givenCommand.taskStatus(), task.getStatus());
        assertEquals(givenCommand.assigneeId(), task.getAssignee());
        assertEquals(givenCommand.dueDate(), task.getDueDate());
        assertEquals(givenCommand.priority(), task.getPriority());

        verify(taskRepositoryPort).save(eq(task));
        verify(eventPublisher).publish(eventsCaptor.capture());

        List<DomainEvent> events = eventsCaptor.getValue();
        assertEquals(6, events.size());
    }

    @Test
    void updateTask_shouldThrowEntityNotFoundException_whenTaskDoesNotExist() {
        final var givenTaskId = TestUtils.randomTaskId();
        final var givenCommand = UpdateTaskCommand.builder()
                .title("Updated title")
                .description("Updated description")
                .build();
        doReturn(Optional.empty()).when(taskRepositoryPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.updateTask(TestUtils.randomUserId(), givenTaskId, givenCommand)
        );
        verify(taskRepositoryPort, times(0)).save(any(Task.class));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void updateStatus_shouldSaveUpdatedTask_whenAllConditionsMet() throws UseCaseException {
        final var task = randomTask();
        final var givenActorId = task.getAssignee();
        final var givenTaskId = task.getId();
        final var givenTaskStatus = "Done";
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(givenTaskId));
        doReturn(DEFAULT_TASK_STATUSES).when(projectService).getAvailableTaskStatuses(eq(task.getProject()));
        ArgumentCaptor<List<DomainEvent>> eventsCaptor = ArgumentCaptor.captor();

        taskService.updateStatus(givenActorId, givenTaskId, givenTaskStatus);

        assertEquals(givenTaskStatus, task.getStatus());
        verify(taskRepositoryPort).save(eq(task));
        verify(eventPublisher).publish(eventsCaptor.capture());
        assertEquals(1, eventsCaptor.getValue().size());
    }

    @Test
    void updateStatus_shouldThrowEntityNotFoundException_whenTaskNotFound() {
        final var givenActorId = TestUtils.randomUserId();
        final var givenTaskId = TestUtils.randomTaskId();
        final var givenTaskStatus = "Done";
        doReturn(Optional.empty()).when(taskRepositoryPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.updateStatus(givenActorId, givenTaskId, givenTaskStatus)
        );
        verify(taskRepositoryPort, times(0)).save(any(Task.class));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void updatePriority_shouldUpdateTask_whenAllConditionsMet() throws UseCaseException {
        final var givenActorId = TestUtils.randomUserId();
        final var task = randomTask();
        final var givenPriority = TaskPriority.HIGH;
        final var eventsCaptor = ArgumentCaptor.forClass(List.class);
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(task.getId()));

        taskService.updatePriority(givenActorId, task.getId(), givenPriority);

        assertEquals(givenPriority, task.getPriority());
        verify(taskRepositoryPort).save(eq(task));
        verify(eventPublisher).publish(eventsCaptor.capture());
        assertEquals(1, eventsCaptor.getAllValues().size());
    }

    @Test
    void updatePriority_shouldThrowEntityNotFoundException_whenTaskDoesNotExist() throws UseCaseException {
        final var givenActorId = TestUtils.randomUserId();
        final var givenTaskId = TestUtils.randomTaskId();
        final var givenPriority = TaskPriority.HIGH;
        doReturn(Optional.empty()).when(taskRepositoryPort).find(eq(givenTaskId));

        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.updatePriority(givenActorId, givenTaskId, givenPriority)
        );

        verify(taskRepositoryPort, times(0)).save(any(Task.class));
        verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    void assignTask_shouldSaveUpdatedTask_whenAllConditionsMet() throws UseCaseException {
        final var task = randomTask();
        final var assignee = randomMemberView();
        final var givenActorId = task.getAssignee();
        final var givenTaskId = task.getId();
        final var givenAssigneeId = assignee.id();
        ArgumentCaptor<List<DomainEvent>> eventsCaptor = ArgumentCaptor.captor();
        doReturn(true).when(projectService).isMember(eq(givenAssigneeId), eq(task.getProject()));
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(givenTaskId));

        taskService.assignTask(givenActorId, givenTaskId, givenAssigneeId);

        assertEquals(givenAssigneeId, task.getAssignee());
        verify(taskRepositoryPort).save(eq(task));
        verify(eventPublisher).publish(eventsCaptor.capture());
        assertEquals(1, eventsCaptor.getValue().size());
    }


    @Test
    void assignTask_shouldThrowEntityNotFoundException_whenTaskNotFound() {
        final var givenActorId = TestUtils.randomUserId();
        final var givenTaskId = TestUtils.randomTaskId();
        final var givenAssigneeId = TestUtils.randomUserId();
        doReturn(Optional.empty()).when(taskRepositoryPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.assignTask(givenActorId, givenTaskId, givenAssigneeId)
        );
        verify(taskRepositoryPort, times(0)).save(any(Task.class));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void assignTask_shouldThrowIllegalAccessException_whenActorDoesNotHaveAccess() {
        final var task = randomTask();
        final var givenActorId = TestUtils.randomUserId();
        final var givenTaskId = task.getId();
        final var givenAssigneeId = TestUtils.randomUserId();
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> taskService.assignTask(givenActorId, givenTaskId, givenAssigneeId)
        );
        verify(taskRepositoryPort, times(0)).save(any(Task.class));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void addComment_shouldSaveNewComment_whenAllConditionsMet() throws UseCaseException {
        final var task = randomTask();
        final var givenActorId = TestUtils.randomUserId();
        final var givenTaskId = task.getId();
        final var givenComment = "Please, help!";
        doReturn(Optional.of(task)).when(taskRepositoryPort).find(eq(givenTaskId));
        taskService.addComment(givenActorId, givenTaskId, givenComment);
        verify(taskCommentRepositoryPort).save(argThat(taskComment -> {
            assertNotNull(taskComment.getCreatedAt());
            assertEquals(givenTaskId, taskComment.getTask());
            assertEquals(givenActorId, taskComment.getAuthor());
            assertEquals(givenComment, taskComment.getContent());
            return true;
        }));
    }

    @Test
    void addComment_shouldThrowEntityNotFoundException_whenDoesNotExist() {
        final var givenActorId = TestUtils.randomUserId();
        final var givenTaskId = TestUtils.randomTaskId();
        final var givenComment = "Please, help!";
        doReturn(Optional.empty()).when(taskRepositoryPort).find(eq(givenTaskId));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> taskService.addComment(givenActorId, givenTaskId, givenComment)
        );
        verify(taskCommentRepositoryPort, times(0)).save(any());
    }

    @Test
    void getTaskDetails_shouldReturnTaskDetails_whenAllConditionsMet() throws UseCaseException {
        final var expected = randomTaskDetails();
        final var givenActorId = TestUtils.randomUserId();
        final var givenTaskId = expected.id();
        doReturn(Optional.of(expected)).when(taskRepositoryPort).findTaskDetails(eq(givenTaskId));
        assertEquals(expected, taskService.getTaskDetails(givenActorId, givenTaskId));
    }

    @Test
    void getTaskDetails_shouldThrowEntityNotFoundException_whenTaskNotFound() {
        final var givenActorId = TestUtils.randomUserId();
        final var givenTaskId = TestUtils.randomTaskId();
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
                .projectId(TestUtils.randomProjectId())
                .build();
        final var givenActorId = TestUtils.randomUserId();
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
                .id(TestUtils.randomTaskId())
                .number(TestUtils.randomTaskNumber())
                .createdAt(Instant.now())
                .project(TestUtils.randomProjectId())
                .status("In progress")
                .title("Title")
                .description("Description")
                .owner(TestUtils.randomUserId())
                .priority(TaskPriority.LOWEST)
                .assignee(TestUtils.randomUserId())
                .build();
    }

    private static TaskDetails randomTaskDetails() {
        return TaskDetails.builder()
                .id(TestUtils.randomTaskId())
                .number(TestUtils.randomTaskNumber())
                .createdAt(Instant.now())
                .dueDate(LocalDate.now().plusWeeks(1))
                .projectId(TestUtils.randomProjectId())
                .status("Done")
                .title("Title")
                .description("Description")
                .priority(TaskPriority.MEDIUM)
                .owner(randomUserInfo())
                .assignee(randomUserInfo())
                .changeLogs(List.of(
                        TaskChangeLogView.builder()
                                .time(Instant.now().minus(Duration.ofHours(1)))
                                .actor(randomUserInfo())
                                .targetProperty(TaskProperty.TITLE)
                                .initialValue("Old")
                                .newValue("Title")
                                .build()
                ))
                .build();
    }

    private static List<TaskPreview> randomTaskPreviews(int total) {
         return IntStream.range(0, total)
                 .mapToObj(value -> randomTaskPreview())
                 .toList();
    }

    private static TaskPreview randomTaskPreview() {
        return TaskPreview.builder()
                .id(TestUtils.randomTaskId())
                .number(TestUtils.randomTaskNumber())
                .createdAt(Instant.now())
                .dueDate(LocalDate.now().minusWeeks(1))
                .title("Title")
                .status("To do")
                .priority(TaskPriority.MEDIUM)
                .assignee(randomUserInfo())
                .build();
    }
}