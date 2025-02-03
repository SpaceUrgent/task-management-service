package com.task.management.application.service;

import com.task.management.application.dto.CreateTaskDTO;
import com.task.management.application.dto.TaskDTO;
import com.task.management.application.dto.TaskDetailsDTO;
import com.task.management.application.dto.UpdateTaskInfoDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.ProjectUser;
import com.task.management.application.model.Task;
import com.task.management.application.model.TaskId;
import com.task.management.application.model.TaskStatus;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.CreateTaskUseCase;
import com.task.management.application.port.in.UpdateTaskInfoUseCase;
import com.task.management.application.port.out.AddTaskPort;
import com.task.management.application.port.out.FindTaskByIdPort;
import com.task.management.application.port.out.UpdateTaskPort;
import com.task.management.application.service.mapper.Mappers;
import com.task.management.application.service.mapper.TaskDetailsMapper;
import com.task.management.application.service.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;

import static com.task.management.application.service.Validation.projectIdRequired;
import static com.task.management.application.service.Validation.taskIdRequired;
import static com.task.management.application.service.Validation.userIdRequired;

@RequiredArgsConstructor
public class TaskService implements CreateTaskUseCase,
                                    UpdateTaskInfoUseCase {
    private final ValidationService validationService;
    private final TaskMapper taskMapper = Mappers.taskMapper();
    private final TaskDetailsMapper taskDetailsMapper = Mappers.taskDetailsMapper();
    private final ProjectService projectService;
    private final AddTaskPort addTaskPort;
    private final FindTaskByIdPort findTaskByIdPort;
    private final UpdateTaskPort updateTaskPort;

    @Override
    public TaskDTO createTask(final UserId currentUser,
                              final ProjectId projectId,
                              final CreateTaskDTO createTaskDTO) throws InsufficientPrivilegesException, EntityNotFoundException {
        userIdRequired(currentUser);
        projectIdRequired(projectId);
        validationService.validate(createTaskDTO);
        projectService.checkUserIsMember(currentUser, projectId);
        final var assignee = new UserId(createTaskDTO.getAssigneeId());
        checkAssigneeIsProjectMember(assignee, projectId);
        var task = Task.builder()
                .project(projectId)
                .title(createTaskDTO.getTitle())
                .description(createTaskDTO.getDescription())
                .status(TaskStatus.TO_DO)
                .owner(ProjectUser.withId(currentUser))
                .assignee(ProjectUser.withId(assignee))
                .build();
        task = addTaskPort.add(task);
        return taskMapper.toDTO(task);
    }

    @Override
    public TaskDetailsDTO updateTask(final UserId currentUser,
                                     final TaskId taskId,
                                     final UpdateTaskInfoDTO updateTaskInfoDTO) throws InsufficientPrivilegesException, EntityNotFoundException {
        userIdRequired(currentUser);
        taskIdRequired(taskId);
        validationService.validate(updateTaskInfoDTO);
        var task = findOrThrow(taskId);
        checkUserIsOwner(currentUser, task);
        task.setTitle(updateTaskInfoDTO.getTitle());
        task.setDescription(updateTaskInfoDTO.getDescription());
        task = updateTaskPort.update(task);
        return taskDetailsMapper.toDTO(task);
    }

    private Task findOrThrow(TaskId id) throws EntityNotFoundException {
        taskIdRequired(id);
        return findTaskByIdPort.find(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %d not found".formatted(id.value())));
    }

    private void checkAssigneeIsProjectMember(UserId assignee, ProjectId id) throws EntityNotFoundException {
        if (!projectService.isProjectMember(assignee, id)) {
            throw new EntityNotFoundException("Project member with id %d not found".formatted(assignee.value()));
        }
    }

    private void checkUserIsOwner(UserId currentUser, Task task) throws InsufficientPrivilegesException {
        if (!task.isOwner(currentUser)) {
            throw new InsufficientPrivilegesException("Current user is not allowed modify task");
        }
    }
}
