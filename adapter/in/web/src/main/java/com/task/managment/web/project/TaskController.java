package com.task.managment.web.project;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.command.UpdateTaskCommand;
import com.task.management.application.project.port.in.AssignTaskUseCase;
import com.task.management.application.project.port.in.GetTaskDetailsUseCase;
import com.task.management.application.project.port.in.UpdateTaskStatusUseCase;
import com.task.management.application.project.port.in.UpdateTaskUseCase;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskId;
import com.task.managment.web.common.BaseController;
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
        final var taskDetails = getTaskDetailsUseCase.getTaskDetails(actor(), new TaskId(taskId));
        return taskDetailsDtoMapper.toDto(taskDetails);
    }

    @PutMapping("/{taskId}")
    public void updateTask(@PathVariable Long taskId,
                           @RequestBody @Valid @NotNull UpdateTaskRequest request) throws UseCaseException {
        final var command = UpdateTaskCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assigneeId(new UserId(request.getAssigneeId()))
                .taskStatus(request.getStatus())
                .dueDate(request.getDueDate())
                .build();
        updateTaskUseCase.updateTask(actor(), new TaskId(taskId), command);
    }

    @PatchMapping("/{taskId}/status")
    public void updateStatus(@PathVariable Long taskId,
                             @RequestBody @Valid @NotNull UpdateTaskStatusRequest request) throws UseCaseException {
        updateTaskStatusUseCase.updateStatus(actor(), new TaskId(taskId), request.getStatus());
    }

    @PatchMapping("/{taskId}/assign")
    public void assignTask(@PathVariable Long taskId,
                           @RequestBody @Valid @NotNull AssignTaskRequest request) throws UseCaseException {
        assignTaskUseCase.assignTask(actor(), new TaskId(taskId), new UserId(request.getAssigneeId()));
    }
}
