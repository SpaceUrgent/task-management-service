package com.task.management.persistence.jpa.repository;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.shared.projection.Page;
import com.task.management.application.project.port.out.TaskRepositoryPort;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.application.project.projection.TaskPreview;
import com.task.management.application.project.query.FindTasksQuery;
import com.task.management.domain.project.model.*;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskChangeLog;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.TaskNumber;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.persistence.jpa.dao.*;
import com.task.management.persistence.jpa.entity.TaskChangeLogEntity;
import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.mapper.TaskMapper;
import com.task.management.persistence.jpa.query.FindTasksQueryAdapter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;
import static java.util.Objects.requireNonNull;

@AppComponent
@RequiredArgsConstructor
public class JpaTaskRepositoryAdapter implements TaskRepositoryPort {
    private final TaskEntityDao taskEntityDao;
    private final TaskChangeLogEntityDao taskChangeLogEntityDao;
    private final TaskNumberSequenceDao taskNumberSequenceDao;
    private final ProjectEntityDao projectEntityDao;
    private final UserEntityDao userEntityDao;
    private final TaskMapper taskMapper = TaskMapper.INSTANCE;

    @Override
    public Task save(Task task) {
        taskRequired(task);
        var taskEntity = buildTaskEntity(task);
        taskEntity = taskEntityDao.save(taskEntity);
        return taskMapper.toTask(taskEntity);
    }

    @Override
    public void save(TaskChangeLog taskChangeLog) {
        parameterRequired(taskChangeLog, "Task change log");
        final var changeLogEntity = TaskChangeLogEntity.builder()

                .occurredAt(taskChangeLog.time())
                .task(taskEntityDao.getReference(taskChangeLog.taskId().value()))
                .actor(userEntityDao.getReference(taskChangeLog.actorId().value()))
                .taskProperty(taskChangeLog.targetProperty())
                .oldValue(taskChangeLog.initialValue())
                .newValue(taskChangeLog.newValue())
                .build();
        taskChangeLogEntityDao.save(changeLogEntity);
    }

    @Override
    public Optional<Task> find(final TaskId id) {
        taskIdRequired(id);
        return taskEntityDao.findById(id.value()).map(taskMapper::toTask);
    }

    @Override
    public Optional<TaskDetails> findTaskDetails(TaskId id) {
        taskIdRequired(id);
        return taskEntityDao.findById(id.value(), "task-details").map(taskMapper::toTaskDetails);
    }

    @Override
    public Page<TaskPreview> findProjectTasks(FindTasksQuery query) {
        requireNonNull(query, "Find tasks query is required");
        final var taskEntityPage = taskEntityDao.findPage(new FindTasksQueryAdapter(query));
        return Page.<TaskPreview>builder()
                .pageNo(query.getPageNumber())
                .pageSize(query.getPageSize())
                .total((int) taskEntityPage.total())
                .totalPages(taskEntityPage.totalPages())
                .content(taskEntityPage.stream().map(taskMapper::toTaskPreview).toList())
                .build();
    }

    @Override
    public boolean projectTaskWithStatusExists(ProjectId projectId, String statusName) {
        parameterRequired(projectId, "Project id");
        notBlank(statusName, "Status name");
        return taskEntityDao.existsWithProjectIdAndStatus(projectId.value(), statusName);
    }

    private TaskEntity buildTaskEntity(Task task) {
        final var projectId = task.getProject().value();
        final var ownerReference = userEntityDao.getReference(task.getOwner().value());
        final var assigneeReference = Optional.ofNullable(task.getAssignee())
                .map(UserId::value)
                .map(userEntityDao::getReference)
                .orElse(null);
        final var projectReference = projectEntityDao.getReference(projectId);
        return TaskEntity.builder()
                .id(Optional.ofNullable(task.getId()).map(TaskId::value).orElse(null))
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .dueDate(task.getDueDate())
                .number(Optional.ofNullable(task.getNumber()).map(TaskNumber::value).orElseGet(() -> taskNumberSequenceDao.nextNumber(projectId)))
                .title(task.getTitle())
                .description(task.getDescription())
                .statusName(task.getStatus())
                .priority(task.getPriority().order())
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
