package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.port.in.command.UpdateTaskCommand;

public interface UpdateTaskUseCase {
    Task updateTask(ProjectUserId actorId, UpdateTaskCommand command) throws UseCaseException;
}
