package com.task.management.domain.project.port.out;

import com.task.management.domain.common.projection.Page;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.projection.TaskDetails;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.projection.TaskPreview;
import com.task.management.domain.project.application.query.FindTasksQuery;

import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);

    Optional<Task> find(TaskId id);

    Optional<TaskDetails> findTaskDetails(TaskId id);

    Page<TaskPreview> findProjectTasks(FindTasksQuery query);
}
