package com.task.management.domain.project.port.in;

import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.application.command.UpdateTaskCommand;

public interface UpdateTaskUseCase {
    void updateTask(UserId actorId, TaskId id, UpdateTaskCommand command) throws UseCaseException;
}
