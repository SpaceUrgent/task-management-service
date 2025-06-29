package com.task.management.application.project.port.out;

import com.task.management.application.shared.projection.Page;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.application.project.projection.TaskPreview;
import com.task.management.application.project.query.FindTasksQuery;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskChangeLog;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.UserId;

import java.util.Optional;
import java.util.stream.Stream;

public interface TaskRepositoryPort {
    Task save(Task task);

    void save(TaskChangeLog taskChangeLog);

    Optional<Task> find(TaskId id);

    Optional<TaskDetails> findTaskDetails(TaskId id);

    Stream<Task> findAllByAssigneeAndProject(UserId assigneeId, ProjectId projectId);

    Page<TaskPreview> findProjectTasks(FindTasksQuery query);

    boolean projectTaskWithStatusExists(ProjectId projectId, String statusName);

    void unassignTasksFrom(UserId assigneeId, ProjectId projectId);
}
