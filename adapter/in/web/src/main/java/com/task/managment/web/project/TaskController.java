package com.task.managment.web.project;

import com.task.management.application.shared.UseCaseException;
import com.task.management.application.project.command.UpdateTaskCommand;
import com.task.management.application.project.port.in.TaskUseCase;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.TaskPriority;
import com.task.managment.web.shared.BaseController;
import com.task.managment.web.project.dto.TaskDetailsDto;
import com.task.managment.web.project.dto.request.*;
import com.task.managment.web.project.mapper.TaskMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController extends BaseController {
    private final TaskUseCase taskUseCase;
    private final TaskMapper taskDetailsDtoMapper;

    @GetMapping("/{taskId}")
    public TaskDetailsDto getTaskDetails(@PathVariable Long taskId) throws UseCaseException {
        final var taskDetails = taskUseCase.getTaskDetails(actor(), new TaskId(taskId));
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
        taskUseCase.updateTask(actor(), new TaskId(taskId), command);
    }

    @PatchMapping("/{taskId}/status")
    public void updateStatus(@PathVariable Long taskId,
                             @RequestBody @Valid @NotNull UpdateTaskStatusRequest request) throws UseCaseException {
        taskUseCase.updateStatus(actor(), new TaskId(taskId), request.getStatus());
    }

    @PatchMapping("/{taskId}/assign")
    public void assignTask(@PathVariable Long taskId,
                           @RequestBody @Valid @NotNull AssignTaskRequest request) throws UseCaseException {
        final var assignee = Optional
                .ofNullable(request.getAssigneeId())
                .map(UserId::new)
                .orElse(null);
        taskUseCase.assignTask(actor(), new TaskId(taskId), assignee);
    }

    @PatchMapping("/{taskId}/priority")
    public void updatePriority(@PathVariable Long taskId,
                               @RequestBody @Valid @NotNull UpdateTaskPriorityRequest request) throws UseCaseException {
        taskUseCase.updatePriority(actor(), new TaskId(taskId), TaskPriority.withPriorityName(request.getPriority()));
    }

    @PostMapping("/{taskId}/comments")
    public void addComment(@PathVariable Long taskId,
                           @RequestBody @Valid @NotNull AddCommentRequest request) throws UseCaseException {
        taskUseCase.addComment(actor(), new TaskId(taskId), request.getComment());
    }
}
