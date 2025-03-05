package com.task.managment.web.controller;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.ProjectUser;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.TaskDetails;
import com.task.management.application.project.model.TaskId;
import com.task.management.application.project.model.TaskStatus;
import com.task.management.application.project.port.in.AssignTaskUseCase;
import com.task.management.application.project.port.in.GetTaskDetailsUseCase;
import com.task.management.application.project.port.in.UpdateTaskStatusUseCase;
import com.task.management.application.project.port.in.UpdateTaskUseCase;
import com.task.management.application.project.port.in.command.UpdateTaskCommand;
import com.task.managment.web.dto.ProjectUserDto;
import com.task.managment.web.dto.TaskDetailsDto;
import com.task.managment.web.dto.request.AssignTaskRequest;
import com.task.managment.web.dto.request.UpdateTaskRequest;
import com.task.managment.web.mapper.TaskDetailsDtoMapper;
import com.task.managment.web.security.SecuredUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final GetTaskDetailsUseCase getTaskDetailsUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;
    private final AssignTaskUseCase assignTaskUseCase;
    private final TaskDetailsDtoMapper taskDetailsDtoMapper;

    @GetMapping("/{taskId}")
    public TaskDetailsDto getTaskDetails(@PathVariable Long taskId) throws UseCaseException {
        final var taskDetails = getTaskDetailsUseCase.getTaskDetails(currentUserId(), new TaskId(taskId));
        return taskDetailsDtoMapper.toDto(taskDetails);
    }

    @PutMapping("/{taskId}")
    public void updateTask(@PathVariable Long taskId,
                           @RequestBody @Valid @NotNull UpdateTaskRequest request) throws UseCaseException {
        final var command = UpdateTaskCommand.builder()
                .taskId(new TaskId(taskId))
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        updateTaskUseCase.updateTask(currentUserId(), command);
    }

    @PatchMapping("/{taskId}/status")
    public void updateStatus(@PathVariable Long taskId,
                             @RequestBody @Valid @NotNull UpdateTaskStatusRequest request) throws UseCaseException {
        updateTaskStatusUseCase.updateStatus(currentUserId(), new TaskId(taskId), new TaskStatus(request.getStatus()));
    }

    @PatchMapping("/{taskId}/assign")
    public void assignTask(@PathVariable Long taskId,
                           @RequestBody @Valid @NotNull AssignTaskRequest request) throws UseCaseException {
        assignTaskUseCase.assignTask(currentUserId(), new TaskId(taskId), new ProjectUserId(request.getAssigneeId()));
    }


    private ProjectUserId currentUserId() {
        return currentUser().getProjectUserId();
    }

    private SecuredUser currentUser() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecuredUser) authentication.getPrincipal());
    }
}
