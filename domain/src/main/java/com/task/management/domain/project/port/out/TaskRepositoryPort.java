package com.task.management.domain.project.port.out;

import com.task.management.domain.common.Page;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.model.TaskDetails;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.model.TaskPreview;
import com.task.management.domain.project.port.in.query.FindTasksQuery;

import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);

    Optional<Task> find(TaskId id);

    Optional<TaskDetails> findTaskDetails(TaskId id);

    Page<TaskPreview> findProjectTasks(FindTasksQuery query);
}
