package com.task.management.domain.project.application.service;

import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.projection.Page;
import com.task.management.domain.common.annotation.UseCase;
import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.validation.ValidationService;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.projection.TaskDetails;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.projection.TaskPreview;
import com.task.management.domain.project.port.in.AssignTaskUseCase;
import com.task.management.domain.project.port.in.FindTasksUseCase;
import com.task.management.domain.project.port.in.GetTaskDetailsUseCase;
import com.task.management.domain.project.port.in.UpdateTaskStatusUseCase;
import com.task.management.domain.project.port.in.UpdateTaskUseCase;
import com.task.management.domain.project.application.command.UpdateTaskCommand;
import com.task.management.domain.project.application.query.FindTasksQuery;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.model.TaskStatus;
import com.task.management.domain.project.port.in.CreateTaskUseCase;
import com.task.management.domain.project.application.command.CreateTaskCommand;
import com.task.management.domain.project.port.out.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

import static com.task.management.domain.common.validation.Validation.actorIdRequired;
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
    public void createTask(final UserId actorId,
                           final ProjectId projectId,
                           final CreateTaskCommand command) throws UseCaseException {
        actorIdRequired(actorId);
        parameterRequired(projectId, "Project id");
        validationService.validate(command);
        projectService.checkIsMember(actorId, projectId);
        checkAssigneeIsMember(command.assigneeId(), projectId);
        var task = Task.builder()
                .createdAt(Instant.now())
                .dueDate(command.dueDate())
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
    public void updateTask(final UserId actorId,
                           final TaskId taskId,
                           final UpdateTaskCommand command) throws UseCaseException {
        actorIdRequired(actorId);
        taskIdRequired(taskId);
        validationService.validate(command);
        var task = findOrThrow(taskId);
        projectService.checkIsMember(actorId, task.getProject());
        task.updateTitle(command.title());
        task.updateDescription(command.description());
        task.setDueDate(command.dueDate());
        taskRepositoryPort.save(task);
    }

    @UseCase
    @Override
    public void updateStatus(final UserId actorId,
                             final TaskId taskId,
                             final TaskStatus status) throws UseCaseException {
        actorIdRequired(actorId);
        taskIdRequired(taskId);
        parameterRequired(status, "Task status");
        final var task = findOrThrow(taskId);
        projectService.checkIsMember(actorId, task.getProject());
        task.updateStatus(status);
        taskRepositoryPort.save(task);
    }

    @UseCase
    @Override
    public void assignTask(final UserId actorId,
                           final TaskId taskId,
                           final UserId assigneeId) throws UseCaseException {
        actorIdRequired(actorId);
        taskIdRequired(taskId);
        parameterRequired(assigneeId, "Assignee id is required");
        final var task = findOrThrow(taskId);
        projectService.checkIsMember(actorId, task.getProject());
        checkAssigneeIsMember(assigneeId, task.getProject());
        task.assignTo(assigneeId);
        taskRepositoryPort.save(task);
    }

    @UseCase
    @Override
    public TaskDetails getTaskDetails(final UserId actorId,
                                      final TaskId taskId) throws UseCaseException {
        actorIdRequired(actorId);
        taskIdRequired(taskId);
        final var taskDetails = findTaskDetailsOrThrow(taskId);
        projectService.checkIsMember(actorId, taskDetails.projectId());
        return taskDetails;
    }

    @UseCase
    @Override
    public Page<TaskPreview> findTasks(final UserId actorId,
                                       final FindTasksQuery query) throws UseCaseException {
        actorIdRequired(actorId);
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

    private void checkAssigneeIsMember(UserId assigneeId, ProjectId projectId) throws UseCaseException.IllegalAccessException {
        if (!projectService.isMember(assigneeId, projectId)) {
            throw projectMemberNotFoundException();
        }
    }

    private static UseCaseException.IllegalAccessException projectMemberNotFoundException() {
        return new UseCaseException.IllegalAccessException("Project member not found");
    }

    private static void taskIdRequired(TaskId taskId) {
        parameterRequired(taskId, "Task id");
    }
}
