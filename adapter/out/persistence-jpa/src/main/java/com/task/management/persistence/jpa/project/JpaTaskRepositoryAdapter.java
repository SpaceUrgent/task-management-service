package com.task.management.persistence.jpa.project;

import com.task.management.domain.common.Page;
import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.model.TaskDetails;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskPreview;
import com.task.management.domain.project.port.in.query.FindTasksQuery;
import com.task.management.domain.project.port.out.TaskRepositoryPort;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.project.mapper.TaskDetailsMapper;
import com.task.management.persistence.jpa.project.mapper.TaskMapper;
import com.task.management.persistence.jpa.project.mapper.TaskPreviewMapper;
import com.task.management.persistence.jpa.query.FindTaskEntityPageQueryAdapter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@AppComponent
@RequiredArgsConstructor
public class JpaTaskRepositoryAdapter implements TaskRepositoryPort {
    private final TaskEntityDao taskEntityDao;
    private final ProjectEntityDao projectEntityDao;
    private final UserEntityDao userEntityDao;
    private final TaskMapper taskMapper = TaskMapper.INSTANCE;
    private final TaskDetailsMapper taskDetailsMapper = TaskDetailsMapper.INSTANCE;
    private final TaskPreviewMapper taskPreviewMapper = TaskPreviewMapper.INSTANCE;

    @Override
    public Task save(Task task) {
        taskRequired(task);
        var taskEntity = buildTaskEntity(task);
        taskEntity = taskEntityDao.save(taskEntity);
        return taskMapper.toModel(taskEntity);
    }

    @Override
    public Optional<Task> find(final TaskId id) {
        taskIdRequired(id);
        return taskEntityDao.findById(id.value()).map(taskMapper::toModel);
    }

    @Override
    public Optional<TaskDetails> findTaskDetails(TaskId id) {
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

    private TaskEntity buildTaskEntity(Task task) {
        final var ownerReference = userEntityDao.getReference(task.getOwner().value());
        final var assigneeReference = userEntityDao.getReference(task.getAssignee().value());
        final var projectReference = projectEntityDao.getReference(task.getProject().value());
        return TaskEntity.builder()
                .id(Optional.ofNullable(task.getId()).map(TaskId::value).orElse(null))
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .owner(ownerReference)
                .assignee(assigneeReference)
                .project(projectReference)
                .build();
    }

    private static void taskIdRequired(TaskId id) {
        requireNonNull(id, "Task id is required");
    }

    private static void taskRequired(Task task) {
        requireNonNull(task, "Task is required");
    }
}
