package com.task.management.application.project.port.out;

import com.task.management.application.common.projection.Page;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.application.project.projection.TaskPreview;
import com.task.management.application.project.query.FindTasksQuery;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskChangeLog;
import com.task.management.domain.shared.model.objectvalue.TaskId;

import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);

    void save(TaskChangeLog taskChangeLog);

    Optional<Task> find(TaskId id);

    Optional<TaskDetails> findTaskDetails(TaskId id);

    Page<TaskPreview> findProjectTasks(FindTasksQuery query);

    boolean projectTaskWithStatusExists(ProjectId projectId, String statusName);
}
