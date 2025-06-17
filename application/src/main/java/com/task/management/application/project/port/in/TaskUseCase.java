package com.task.management.application.project.port.in;

import com.task.management.application.project.command.CreateTaskCommand;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.application.project.projection.TaskPreview;
import com.task.management.application.project.query.FindTasksQuery;
import com.task.management.application.shared.UseCaseException;
import com.task.management.application.project.command.UpdateTaskCommand;
import com.task.management.application.shared.projection.Page;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.TaskPriority;

public interface TaskUseCase {
    Page<TaskPreview> findTasks(UserId actorId, FindTasksQuery query) throws UseCaseException;

    TaskDetails getTaskDetails(UserId actorId, TaskId taskId) throws UseCaseException;

    void createTask(UserId actorId, ProjectId projectId, CreateTaskCommand command) throws UseCaseException;

    void updateTask(UserId actorId, TaskId id, UpdateTaskCommand command) throws UseCaseException;

    void updateStatus(UserId actorId, TaskId taskId, String statusName) throws UseCaseException;

    void updatePriority(UserId actorId, TaskId taskId, TaskPriority priority) throws UseCaseException;

    void assignTask(UserId actorId, TaskId taskId, UserId assigneeId) throws UseCaseException;

    void addComment(UserId actorId, TaskId taskId, String comment) throws UseCaseException;
}
