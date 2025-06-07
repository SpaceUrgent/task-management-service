package com.task.managment.web.project;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.command.UpdateTaskCommand;
import com.task.management.application.project.port.in.GetTaskDetailsUseCase;
import com.task.management.application.project.port.in.UpdateTaskUseCase;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.common.model.objectvalue.TaskId;
import com.task.management.domain.common.model.objectvalue.TaskPriority;
import com.task.managment.web.common.BaseController;
import com.task.managment.web.project.dto.TaskDetailsDto;
import com.task.managment.web.project.dto.request.*;
import com.task.managment.web.project.mapper.TaskMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController extends BaseController {
    private final GetTaskDetailsUseCase getTaskDetailsUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
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
                .priority(TaskPriority.withPriorityName(request.getPriority()))
                .dueDate(request.getDueDate())
                .build();
        updateTaskUseCase.updateTask(actor(), new TaskId(taskId), command);
    }

    @PatchMapping("/{taskId}/status")
    public void updateStatus(@PathVariable Long taskId,
                             @RequestBody @Valid @NotNull UpdateTaskStatusRequest request) throws UseCaseException {
        updateTaskUseCase.updateStatus(actor(), new TaskId(taskId), request.getStatus());
    }

    @PatchMapping("/{taskId}/assign")
    public void assignTask(@PathVariable Long taskId,
                           @RequestBody @Valid @NotNull AssignTaskRequest request) throws UseCaseException {
        updateTaskUseCase.assignTask(actor(), new TaskId(taskId), new UserId(request.getAssigneeId()));
    }

    @PatchMapping("/{taskId}/priority")
    public void updatePriority(@PathVariable Long taskId,
                               @RequestBody @Valid @NotNull UpdateTaskPriorityRequest request) throws UseCaseException {
        updateTaskUseCase.updatePriority(actor(), new TaskId(taskId), TaskPriority.withPriorityName(request.getPriority()));
    }

    @PostMapping("/{taskId}/comments")
    public void addComment(@PathVariable Long taskId,
                           @RequestBody @Valid @NotNull AddCommentRequest request) throws UseCaseException {
        updateTaskUseCase.addComment(actor(), new TaskId(taskId), request.getComment());
    }
}
