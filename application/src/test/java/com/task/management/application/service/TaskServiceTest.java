package com.task.management.application.service;

import com.task.management.application.dto.CreateTaskDTO;
import com.task.management.application.dto.UpdateTaskInfoDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.Task;
import com.task.management.application.model.TaskId;
import com.task.management.application.model.TaskStatus;
import com.task.management.application.model.UserId;
import com.task.management.application.port.out.AddTaskPort;
import com.task.management.application.port.out.FindTaskByIdPort;
import com.task.management.application.port.out.UpdateTaskPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Optional;

import static com.task.management.application.service.TestUtils.assertMatches;
import static com.task.management.application.service.TestUtils.randomLong;
import static com.task.management.application.service.TestUtils.randomProjectId;
import static com.task.management.application.service.TestUtils.randomProjectUser;
import static com.task.management.application.service.TestUtils.randomTaskId;
import static com.task.management.application.service.TestUtils.randomUserId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private ValidationService validationService;
    @Mock
    private ProjectService projectService;
    @Mock
    private AddTaskPort addTaskPort;
    @Mock
    private FindTaskByIdPort findTaskByIdPort;
    @Mock
    private UpdateTaskPort updateTaskPort;
    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldReturnNewTaskDTO_whenAllConditionsMet() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var givenUserId = randomUserId();
        final var givenProjectId = randomProjectId();
        final var givenCreateTaskDTO = createTaskDTO();
        final var givenAssigneeId = new UserId(givenCreateTaskDTO.getAssigneeId());
        doReturn(true).when(projectService).isProjectMember(eq(givenAssigneeId), eq(givenProjectId));

        doAnswer(addTaskAnswer()).when(addTaskPort).add(any());
        final var created = taskService.createTask(givenUserId, givenProjectId, givenCreateTaskDTO);
        assertEquals(givenCreateTaskDTO.getTitle(), created.getTitle());
        assertEquals(givenCreateTaskDTO.getAssigneeId(), created.getAssignee().id());
    }

    @Test
    void createTask_shouldThrowEntityNotFoundException_whenAssigneeIsNotProjectMember() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var givenUserId = randomUserId();
        final var givenProjectId = randomProjectId();
        final var givenCreateTaskDTO = createTaskDTO();

        final var givenAssigneeId = new UserId(givenCreateTaskDTO.getAssigneeId());
        doReturn(false).when(projectService).isProjectMember(eq(givenAssigneeId), eq(givenProjectId));

        final var exception = assertThrows(
                EntityNotFoundException.class,
                () -> taskService.createTask(givenUserId, givenProjectId, givenCreateTaskDTO)
        );
        assertEquals(
                "Project member with id %d not found".formatted(givenCreateTaskDTO.getAssigneeId()),
                exception.getMessage()
        );
        verifyNoInteractions(addTaskPort);
    }

    @Test
    void updateTask_shouldReturnUpdatedTaskDetailsDTO_whenAllConditionsMet() throws InsufficientPrivilegesException, EntityNotFoundException {
        final var givenUpdateTaskDTO = updateTaskInfoDTO();
        final var task = randomTask();
        final var givenUserId = task.getOwner().id();
        final var givenTaskId = task.getId();
        doReturn(Optional.of(task)).when(findTaskByIdPort).find(eq(givenTaskId));
        doAnswer(updateTaskAnswer()).when(updateTaskPort).update(any());
        final var updated = taskService.updateTask(givenUserId, givenTaskId, givenUpdateTaskDTO);
        assertEquals(task.getId().value(), updated.id());
        assertEquals(givenUpdateTaskDTO.getTitle(), updated.title());
        assertEquals(givenUpdateTaskDTO.getDescription(), updated.description());
        assertMatches(task.getOwner(), updated.owner());
        assertMatches(task.getAssignee(), updated.assignee());
    }

    private static CreateTaskDTO createTaskDTO() {
        final var createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setTitle("New task");
        createTaskDTO.setDescription("New task description");
        createTaskDTO.setAssigneeId(randomLong());
        return createTaskDTO;
    }

    private static Task randomTask() {
        final var taskIdValue = randomLong();
        final var owner = randomProjectUser();
        return Task.builder()
                .id(new TaskId(taskIdValue))
                .project(randomProjectId())
                .title("Title %d".formatted(taskIdValue))
                .description("Description %d".formatted(taskIdValue))
                .status(TaskStatus.TO_DO)
                .owner(owner)
                .assignee(owner)
                .build();
    }

    private UpdateTaskInfoDTO updateTaskInfoDTO() {
        final var updateTaskInfoDTO = new UpdateTaskInfoDTO();
        updateTaskInfoDTO.setTitle("New title");
        updateTaskInfoDTO.setDescription("New description");
        return updateTaskInfoDTO;
    }

    private static Answer<Task> updateTaskAnswer() {
        return invocation -> {
            final var argument = (Task) invocation.getArgument(0);
            return Task.builder()
                    .id(argument.getId())
                    .project(argument.getProject())
                    .title(argument.getTitle())
                    .description(argument.getDescription())
                    .status(argument.getStatus())
                    .owner(argument.getOwner())
                    .assignee(argument.getAssignee())
                    .build();
        };
    }

    private static Answer<Task> addTaskAnswer() {
        return invocation -> {
            final var argument = (Task) invocation.getArgument(0);
            return Task.builder()
                    .id(randomTaskId())
                    .project(argument.getProject())
                    .title(argument.getTitle())
                    .description(argument.getDescription())
                    .status(argument.getStatus())
                    .owner(argument.getOwner())
                    .assignee(argument.getAssignee())
                    .build();
        };
    }
}