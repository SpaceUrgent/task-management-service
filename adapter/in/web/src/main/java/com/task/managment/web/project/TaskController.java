package com.task.managment.web.project;

import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.port.in.AssignTaskUseCase;
import com.task.management.domain.project.port.in.GetTaskDetailsUseCase;
import com.task.management.domain.project.port.in.UpdateTaskStatusUseCase;
import com.task.management.domain.project.port.in.UpdateTaskUseCase;
import com.task.management.domain.project.application.command.UpdateTaskCommand;
import com.task.managment.web.project.dto.TaskDetailsDto;
import com.task.managment.web.project.dto.request.AssignTaskRequest;
import com.task.managment.web.project.dto.request.UpdateTaskRequest;
import com.task.managment.web.project.dto.request.UpdateTaskStatusRequest;
import com.task.managment.web.project.mapper.TaskMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
public class TaskController extends BaseController {
    private final GetTaskDetailsUseCase getTaskDetailsUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;
    private final AssignTaskUseCase assignTaskUseCase;
    private final TaskMapper taskDetailsDtoMapper;

    @GetMapping("/{taskId}")
    public TaskDetailsDto getTaskDetails(@PathVariable Long taskId) throws UseCaseException {
        final var taskDetails = getTaskDetailsUseCase.getTaskDetails(actorId(), new TaskId(taskId));
        return taskDetailsDtoMapper.toDto(taskDetails);
    }

    @PutMapping("/{taskId}")
    public void updateTask(@PathVariable Long taskId,
                           @RequestBody @Valid @NotNull UpdateTaskRequest request) throws UseCaseException {
        final var command = UpdateTaskCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assigneeId(new ProjectUserId(request.getAssigneeId()))
                .taskStatus(request.getStatus())
                .build();
        updateTaskUseCase.updateTask(actorId(), new TaskId(taskId), command);
    }

    @PatchMapping("/{taskId}/status")
    public void updateStatus(@PathVariable Long taskId,
                             @RequestBody @Valid @NotNull UpdateTaskStatusRequest request) throws UseCaseException {
        updateTaskStatusUseCase.updateStatus(actorId(), new TaskId(taskId), request.getStatus());
    }

    @PatchMapping("/{taskId}/assign")
    public void assignTask(@PathVariable Long taskId,
                           @RequestBody @Valid @NotNull AssignTaskRequest request) throws UseCaseException {
        assignTaskUseCase.assignTask(actorId(), new TaskId(taskId), new ProjectUserId(request.getAssigneeId()));
    }
}
