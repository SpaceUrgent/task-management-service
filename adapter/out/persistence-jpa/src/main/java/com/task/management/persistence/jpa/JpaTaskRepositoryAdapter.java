package com.task.management.persistence.jpa;

import com.task.management.domain.common.Page;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.model.TaskDetails;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskPreview;
import com.task.management.domain.project.port.in.query.FindTasksQuery;
import com.task.management.domain.project.port.out.AddTaskPort;
import com.task.management.domain.project.port.out.FindProjectTasksPort;
import com.task.management.domain.project.port.out.FindTaskByIdPort;
import com.task.management.domain.project.port.out.FindTaskDetailsByIdPort;
import com.task.management.domain.project.port.out.UpdateTaskPort;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.mapper.Mappers;
import com.task.management.persistence.jpa.mapper.TaskDetailsMapper;
import com.task.management.persistence.jpa.mapper.TaskMapper;
import com.task.management.persistence.jpa.mapper.TaskPreviewMapper;
import com.task.management.persistence.jpa.query.FindTaskEntityPageQueryAdapter;
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
    private final TaskEntityDao taskEntityDao;
    private final ProjectEntityDao projectEntityDao;
    private final UserEntityDao userEntityDao;
    private final TaskMapper taskMapper = Mappers.taskMapper;
    private final TaskDetailsMapper taskDetailsMapper = Mappers.taskDetailsMapper;
    private final TaskPreviewMapper taskPreviewMapper = Mappers.taskPreviewMapper;

    @Override
    public Optional<Task> find(final TaskId id) {
        taskIdRequired(id);
        return taskEntityDao.findById(id.value()).map(taskMapper::toModel);
    }

    @Override
    public Optional<TaskDetails> findTaskDetailsById(final TaskId id) {
        taskIdRequired(id);
        return taskEntityDao.findById(id.value()).map(taskDetailsMapper::toModel);
    }

    @Override
    public Page<TaskPreview> findProjectTasks(FindTasksQuery query) {
        requireNonNull(query, "Find tasks query is required");
        final var taskEntityPage = taskEntityDao.findPage(new FindTaskEntityPageQueryAdapter(query));
        return Page.<TaskPreview>builder()
                .pageNo(query.getPageNumber())
                .pageSize(query.getPageSize())
                .total((int) taskEntityPage.total())
                .totalPages(taskEntityPage.totalPages())
                .content(taskEntityPage.stream().map(taskPreviewMapper::toModel).toList())
                .build();
    }

    @Override
    public Task add(final Task task) {
        taskRequired(task);
        final var ownerReference = userEntityDao.getReference(task.getOwner().id().value());
        final var assigneeReference = userEntityDao.getReference(task.getAssignee().id().value());
        final var projectReference = projectEntityDao.getReference(task.getProject().value());
        var taskEntity = TaskEntity.builder()
                .createdAt(task.getCreatedAt())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().value())
                .owner(ownerReference)
                .assignee(assigneeReference)
                .project(projectReference)
                .build();
        taskEntity = taskEntityDao.save(taskEntity);
        return taskMapper.toModel(taskEntity);
    }

    @Override
    public Task update(final Task task) {
        taskRequired(task);
        var taskEntity = getTaskEntity(task.getId());
        final var ownerReference = userEntityDao.getReference(task.getOwner().id().value());
        final var assigneeReference = userEntityDao.getReference(task.getAssignee().id().value());
        taskEntity.setTitle(task.getTitle());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setStatus(task.getStatus().value());
        taskEntity.setOwner(ownerReference);
        taskEntity.setAssignee(assigneeReference);
        taskEntity = taskEntityDao.save(taskEntity);
        return taskMapper.toModel(taskEntity);
    }

    private TaskEntity getTaskEntity(TaskId id) {
        final var entityId = id.value();
        return taskEntityDao.findById(entityId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id %d not found".formatted(entityId)));
    }

    private void taskIdRequired(TaskId id) {
        requireNonNull(id, "Task id is required");
    }

    private static void taskRequired(Task task) {
        requireNonNull(task, "Task is required");
    }
}
