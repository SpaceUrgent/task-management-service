package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.command.UpdateTaskCommand;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.TaskPriority;

public interface UpdateTaskUseCase {
    void updateTask(UserId actorId, TaskId id, UpdateTaskCommand command) throws UseCaseException;

    void updateStatus(UserId actorId, TaskId taskId, String statusName) throws UseCaseException;

    void updatePriority(UserId actorId, TaskId taskId, TaskPriority priority) throws UseCaseException;

    void assignTask(UserId actorId, TaskId taskId, UserId assigneeId) throws UseCaseException;

    void addComment(UserId actorId, TaskId taskId, String comment) throws UseCaseException;
}
