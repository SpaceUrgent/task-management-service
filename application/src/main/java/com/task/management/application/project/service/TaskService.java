package com.task.management.application.project.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.ValidationService;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.TaskId;
import com.task.management.application.project.port.in.UpdateTaskUseCase;
import com.task.management.application.project.port.in.command.UpdateTaskCommand;
import com.task.management.application.project.port.out.FindTaskByIdPort;
import com.task.management.application.project.port.out.SaveTaskPort;
import com.task.management.application.project.model.Task;
import com.task.management.application.project.model.TaskStatus;
import com.task.management.application.project.port.in.CreateTaskUseCase;
import com.task.management.application.project.port.in.command.CreateTaskCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TaskService implements CreateTaskUseCase,
                                    UpdateTaskUseCase
{
    private final ValidationService validationService;
    private final ProjectUserService projectUserService;
    private final SaveTaskPort saveTaskPort;
    private final FindTaskByIdPort findTaskByIdPort;

    @Override
    public Task createTask(final ProjectUserId actorId,
                           final CreateTaskCommand command) throws UseCaseException {
        validationService.validate(command);
        final var projectId = command.projectId();
        final var owner = projectUserService.findProjectMember(actorId, projectId)
                .orElseThrow(TaskService::doesNotHavaAccessException);
        final var assignee = projectUserService.findProjectMember(command.assigneeId(), projectId)
                .orElseThrow(TaskService::projectMemberNotFoundException);
        var task = Task.builder()
                .project(projectId)
                .title(command.title())
                .description(command.description())
                .status(TaskStatus.TO_DO)
                .owner(owner)
                .assignee(assignee)
                .build();
        return saveTaskPort.save(task);
    }

    public Task updateTask(final ProjectUserId actorId,
                           final UpdateTaskCommand command) throws UseCaseException {
        validationService.validate(command);
        var task = findOrThrow(command.taskId());
        checkUserIsOwner(actorId, task);
        task.setTitle(command.title());
        task.setDescription(command.description());
        return saveTaskPort.save(task);
    }

    private Task findOrThrow(TaskId id) throws UseCaseException.EntityNotFoundException {
        return findTaskByIdPort.find(id)
                .orElseThrow(() -> new UseCaseException.EntityNotFoundException("Task with id %d not found".formatted(id.value())));
    }

    private void checkUserIsOwner(ProjectUserId currentUser, Task task) throws UseCaseException.IllegalAccessException {
        if (!task.isOwner(currentUser)) {
            throw new UseCaseException.IllegalAccessException("Current user is not allowed modify task");
        }
    }

    private static UseCaseException.IllegalAccessException doesNotHavaAccessException() {
        return new UseCaseException.IllegalAccessException("Current user does not have access to project");
    }


    private static UseCaseException.IllegalAccessException projectMemberNotFoundException() {
        return new UseCaseException.IllegalAccessException("Project member not found");
    }
}
