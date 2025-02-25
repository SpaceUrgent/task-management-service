package com.task.management.persistence.jpa;

import com.task.management.application.common.Page;
import com.task.management.application.project.model.Task;
import com.task.management.application.project.model.TaskDetails;
import com.task.management.application.project.model.TaskId;
import com.task.management.application.project.model.TaskPreview;
import com.task.management.application.project.port.in.query.FindTasksQuery;
import com.task.management.application.project.port.out.AddTaskPort;
import com.task.management.application.project.port.out.FindProjectTasksPort;
import com.task.management.application.project.port.out.FindTaskByIdPort;
import com.task.management.application.project.port.out.FindTaskDetailsByIdPort;
import com.task.management.application.project.port.out.UpdateTaskPort;
import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.mapper.TaskDetailsMapper;
import com.task.management.persistence.jpa.mapper.TaskMapper;
import com.task.management.persistence.jpa.mapper.TaskPreviewMapper;
import com.task.management.persistence.jpa.query.FindTaskEntityPageQueryAdapter;
import com.task.management.persistence.jpa.repository.JpaProjectRepository;
import com.task.management.persistence.jpa.repository.JpaTaskRepository;
import com.task.management.persistence.jpa.repository.JpaUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class JpaTaskRepositoryAdapter implements FindTaskByIdPort,
                                                 FindTaskDetailsByIdPort,
                                                 FindProjectTasksPort,
                                                 AddTaskPort,
                                                 UpdateTaskPort {
    private final JpaTaskRepository jpaTaskRepository;
    private final JpaProjectRepository jpaProjectRepository;
    private final JpaUserRepository jpaUserRepository;
    private final TaskMapper taskMapper;
    private final TaskDetailsMapper taskDetailsMapper;
    private final TaskPreviewMapper taskPreviewMapper;

    @Override
    public Optional<Task> find(final TaskId id) {
        taskIdRequired(id);
        return jpaTaskRepository.findById(id.value()).map(taskMapper::toModel);
    }

    @Override
    public Optional<TaskDetails> findTaskDetailsById(final TaskId id) {
        taskIdRequired(id);
        return jpaTaskRepository.findById(id.value()).map(taskDetailsMapper::toModel);
    }

    @Override
    public Page<TaskPreview> findProjectTasks(FindTasksQuery query) {
        requireNonNull(query, "Find tasks query is required");
        final var taskEntityPage = jpaTaskRepository.findPage(new FindTaskEntityPageQueryAdapter(query));
        return Page.<TaskPreview>builder()
                .pageNo(query.getPageNumber())
                .pageSize(query.getPageSize())
                .total((int) taskEntityPage.getTotalElements())
                .totalPages(taskEntityPage.getTotalPages())
                .content(taskEntityPage.get().map(taskPreviewMapper::toModel).toList())
                .build();
    }

    @Override
    public Task add(final Task task) {
        taskRequired(task);
        final var ownerReference = jpaUserRepository.getReferenceById(task.getOwner().id().value());
        final var assigneeReference = jpaUserRepository.getReferenceById(task.getAssignee().id().value());
        final var projectReference = jpaProjectRepository.getReferenceById(task.getProject().value());
        var taskEntity = TaskEntity.builder()
                .createdAt(task.getCreatedAt())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().value())
                .owner(ownerReference)
                .assignee(assigneeReference)
                .project(projectReference)
                .build();
        taskEntity = jpaTaskRepository.save(taskEntity);
        return taskMapper.toModel(taskEntity);
    }

    @Override
    public Task update(final Task task) {
        taskRequired(task);
        var taskEntity = getTaskEntity(task.getId());
        final var ownerReference = jpaUserRepository.getReferenceById(task.getOwner().id().value());
        final var assigneeReference = jpaUserRepository.getReferenceById(task.getAssignee().id().value());
        taskEntity.setTitle(task.getTitle());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setStatus(task.getStatus().value());
        taskEntity.setOwner(ownerReference);
        taskEntity.setAssignee(assigneeReference);
        taskEntity = jpaTaskRepository.save(taskEntity);
        return taskMapper.toModel(taskEntity);
    }

    private TaskEntity getTaskEntity(TaskId id) {
        final var entityId = id.value();
        return jpaTaskRepository.findById(entityId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %d not found".formatted(entityId)));
    }

    private void taskIdRequired(TaskId id) {
        requireNonNull(id, "Task id is required");
    }

    private static void taskRequired(Task task) {
        requireNonNull(task, "Task is required");
    }
}
