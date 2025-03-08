package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.TaskId;
import com.task.management.domain.project.port.in.command.UpdateTaskCommand;

public interface UpdateTaskUseCase {
    void updateTask(ProjectUserId actorId, TaskId id, UpdateTaskCommand command) throws UseCaseException;
}
