package com.task.management.domain.project.service;

import com.task.management.domain.common.Page;
import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.common.ValidationService;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUser;
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
import com.task.management.domain.project.port.out.FindProjectTasksPort;
import com.task.management.domain.project.port.out.FindTaskByIdPort;
import com.task.management.domain.project.port.out.FindTaskDetailsByIdPort;
import com.task.management.domain.project.port.out.AddTaskPort;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.model.TaskStatus;
import com.task.management.domain.project.port.in.CreateTaskUseCase;
import com.task.management.domain.project.port.in.command.CreateTaskCommand;
import com.task.management.domain.project.port.out.UpdateTaskPort;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import static com.task.management.domain.common.Validation.parameterRequired;

@RequiredArgsConstructor
public class TaskService implements CreateTaskUseCase,
                                    UpdateTaskUseCase,
                                    UpdateTaskStatusUseCase,
                                    AssignTaskUseCase,
                                    GetTaskDetailsUseCase,
                                    FindTasksUseCase
{
    private final ValidationService validationService;
    private final ProjectUserService projectUserService;
    private final AddTaskPort saveTaskPort;
    private final UpdateTaskPort updateTaskPort;
    private final FindTaskByIdPort findTaskByIdPort;
    private final FindTaskDetailsByIdPort findTaskDetailsByIdPort;
    private final FindProjectTasksPort findProjectTasksPort;

    @Override
    public Task createTask(final ProjectUserId actorId,
                           final CreateTaskCommand command) throws UseCaseException {
        validationService.validate(command);
        final var projectId = command.projectId();
        final var owner = projectUserService.findProjectMember(actorId, projectId)
                .orElseThrow(TaskService::doesNotHavaAccessException);
        final var assignee = getAssignee(command.assigneeId(), projectId);
        var task = Task.builder()
                .createdAt(Instant.now())
                .project(projectId)
                .title(command.title())
                .description(command.description())
                .status(TaskStatus.TO_DO)
                .owner(owner)
                .assignee(assignee)
                .build();
        return saveTaskPort.add(task);
    }

    @Override
    public Task updateTask(final ProjectUserId actorId,
                           final UpdateTaskCommand command) throws UseCaseException {
        validationService.validate(command);
        var task = findOrThrow(command.taskId());
        checkUserIsOwner(actorId, task);
        task.setTitle(command.title());
        task.setDescription(command.description());
        return updateTaskPort.update(task);
    }

    @Override
    public void updateStatus(final ProjectUserId actorId,
                             final TaskId taskId,
                             final TaskStatus status) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(taskId, "Task id");
        parameterRequired(status, "Task status");
        final var task = findOrThrow(taskId);
        checkAllowedToUpdateStatus(actorId, task);
        task.setStatus(status);
        updateTaskPort.update(task);
    }

    @Override
    public void assignTask(final ProjectUserId actorId,
                           final TaskId taskId,
                           final ProjectUserId assigneeId) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(taskId, "Task id");
        parameterRequired(assigneeId, "Assignee id is required");
        final var task = findOrThrow(taskId);
        checkAllowedToAssign(actorId, task);
        final var assignee = getAssignee(assigneeId, task.getProject());
        task.setAssignee(assignee);
        updateTaskPort.update(task);
    }

    @Override
    public TaskDetails getTaskDetails(final ProjectUserId actorId,
                                      final TaskId taskId) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(taskId, "Task id");
        final var taskDetails = findTaskDetailsOrThrow(taskId);
        checkIsMember(actorId, taskDetails.projectId());
        return taskDetails;
    }

    @Override
    public Page<TaskPreview> findTasks(final ProjectUserId actorId,
                                       final FindTasksQuery query) throws UseCaseException {
        parameterRequired(actorId, "Actor id");
        parameterRequired(query, "Query");
        checkIsMember(actorId, query.getProjectId());
        return findProjectTasksPort.findProjectTasks(query);
    }

    private Task findOrThrow(TaskId id) throws UseCaseException.EntityNotFoundException {
        return findTaskByIdPort.find(id)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("Task with id %d not found".formatted(id.value())));
    }

    private TaskDetails findTaskDetailsOrThrow(TaskId id) throws UseCaseException.EntityNotFoundException {
        return findTaskDetailsByIdPort.findTaskDetailsById(id)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("Task with id %d not found".formatted(id.value())));
    }

    private ProjectUser getAssignee(ProjectUserId assigneeId, ProjectId projectId) throws UseCaseException.IllegalAccessException {
        return projectUserService.findProjectMember(assigneeId, projectId)
                .orElseThrow(TaskService::projectMemberNotFoundException);
    }

    private void checkUserIsOwner(ProjectUserId userId, Task task) throws UseCaseException.IllegalAccessException {
        if (!task.isOwner(userId)) {
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
        return task.isOwner(userId) || task.isAssignee(userId);
    }

    private static UseCaseException.IllegalAccessException doesNotHavaAccessException() {
        return new UseCaseException.IllegalAccessException("Current user does not have access to project");
    }


    private static UseCaseException.IllegalAccessException projectMemberNotFoundException() {
        return new UseCaseException.IllegalAccessException("Project member not found");
    }

    private void checkIsMember(ProjectUserId userId, ProjectId projectId) throws UseCaseException {
        if (!projectUserService.isMember(userId, projectId)) {
            throw new UseCaseException.IllegalAccessException("Current does not have access to project");
        }
    }
}
