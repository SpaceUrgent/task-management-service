package com.task.management.domain.project.service;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.Page;
import com.task.management.domain.common.annotation.UseCase;
import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.common.validation.ValidationService;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskDetails;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskPreview;
import com.task.management.domain.project.port.in.AssignTaskUseCase;
import com.task.management.domain.project.port.in.FindTasksUseCase;
import com.task.management.domain.project.port.in.GetTaskDetailsUseCase;
import com.task.management.domain.project.port.in.UpdateTaskStatusUseCase;
import com.task.management.domain.project.port.in.UpdateTaskUseCase;
import com.task.management.domain.project.port.in.command.UpdateTaskCommand;
import com.task.management.domain.project.port.in.query.FindTasksQuery;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.model.TaskStatus;
import com.task.management.domain.project.port.in.CreateTaskUseCase;
import com.task.management.domain.project.port.in.command.CreateTaskCommand;
import com.task.management.domain.project.port.out.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@AppComponent
@RequiredArgsConstructor
public class TaskService implements CreateTaskUseCase,
                                    UpdateTaskUseCase,
                                    UpdateTaskStatusUseCase,
                                    AssignTaskUseCase,
                                    GetTaskDetailsUseCase,
                                    FindTasksUseCase {
    private final ValidationService validationService;
    private final ProjectService projectService;
    private final TaskRepositoryPort taskRepositoryPort;

    @UseCase
    @Override
    public void createTask(final ProjectUserId actorId,
                           final ProjectId projectId,
                           final CreateTaskCommand command) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(projectId, "Project id");
        validationService.validate(command);
        projectService.checkIsMember(actorId, projectId);
        checkAssigneeIsMember(command.assigneeId(), projectId);
        var task = Task.builder()
                .createdAt(Instant.now())
                .project(projectId)
                .title(command.title())
                .description(command.description())
                .status(TaskStatus.TO_DO)
                .owner(actorId)
                .assignee(command.assigneeId())
                .build();
        taskRepositoryPort.save(task);
    }

    @UseCase
    @Override
    public void updateTask(final ProjectUserId actorId,
                           final TaskId taskId,
                           final UpdateTaskCommand command) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(taskId, "Task id");
        validationService.validate(command);
        var task = findOrThrow(taskId);
        checkUserIsOwner(actorId, task);
        task.updateTitle(command.title());
        task.updateDescription(command.description());
        taskRepositoryPort.save(task);
    }

    @UseCase
    @Override
    public void updateStatus(final ProjectUserId actorId,
                             final TaskId taskId,
                             final TaskStatus status) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(taskId, "Task id");
        parameterRequired(status, "Task status");
        final var task = findOrThrow(taskId);
        checkAllowedToUpdateStatus(actorId, task);
        task.updateStatus(status);
        taskRepositoryPort.save(task);
    }

    @UseCase
    @Override
    public void assignTask(final ProjectUserId actorId,
                                  final TaskId taskId,
                                  final ProjectUserId assigneeId) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(taskId, "Task id");
        parameterRequired(assigneeId, "Assignee id is required");
        final var task = findOrThrow(taskId);
        checkAllowedToAssign(actorId, task);
        checkAssigneeIsMember(assigneeId, task.getProject());
        task.assignTo(assigneeId);
        taskRepositoryPort.save(task);
    }

    @UseCase
    @Override
    public TaskDetails getTaskDetails(final ProjectUserId actorId,
                                      final TaskId taskId) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(taskId, "Task id");
        final var taskDetails = findTaskDetailsOrThrow(taskId);
        projectService.checkIsMember(actorId, taskDetails.projectId());
        return taskDetails;
    }

    @UseCase
    @Override
    public Page<TaskPreview> findTasks(final ProjectUserId actorId,
                                       final FindTasksQuery query) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(query, "Query");
        projectService.checkIsMember(actorId, query.getProjectId());
        return taskRepositoryPort.findProjectTasks(query);
    }

    private Task findOrThrow(TaskId id) throws UseCaseException.EntityNotFoundException {
        return taskRepositoryPort.find(id)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("Task with id %d not found".formatted(id.value())));
    }

    private TaskDetails findTaskDetailsOrThrow(TaskId id) throws UseCaseException.EntityNotFoundException {
        return taskRepositoryPort.findTaskDetails(id)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("Task with id %d not found".formatted(id.value())));
    }

    private void checkAssigneeIsMember(ProjectUserId assigneeId, ProjectId projectId) throws UseCaseException.IllegalAccessException {
        if (!projectService.isMember(assigneeId, projectId)) {
            throw projectMemberNotFoundException();
        }
    }

    private void checkUserIsOwner(ProjectUserId userId, Task task) throws UseCaseException.IllegalAccessException {
        if (!task.isOwnedBy(userId)) {
            throw new UseCaseException.IllegalAccessException("Current user is not allowed modify task");
        }
    }

    private void checkAllowedToUpdateStatus(ProjectUserId userId, Task task) throws UseCaseException.IllegalAccessException {
        if (!hasDirectAccessToTask(userId, task)) {
            throw new UseCaseException.IllegalAccessException("Current user is not allowed to update task status");
        }
    }

    private void checkAllowedToAssign(ProjectUserId userId, Task task) throws UseCaseException.IllegalAccessException {
        if (!hasDirectAccessToTask(userId, task)) {
            throw new UseCaseException.IllegalAccessException("Current user is not allowed to assign task");
        }
    }

    private boolean hasDirectAccessToTask(ProjectUserId userId, Task task) {
        return task.isOwnedBy(userId) || task.isAssignedTo(userId);
    }

    private static UseCaseException.IllegalAccessException projectMemberNotFoundException() {
        return new UseCaseException.IllegalAccessException("Project member not found");
    }
}
