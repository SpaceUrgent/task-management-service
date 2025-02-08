package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.Task;
import com.task.management.application.project.port.in.command.UpdateTaskCommand;

public interface UpdateTaskUseCase {
    Task updateTask(ProjectUserId actorId, UpdateTaskCommand command) throws UseCaseException;
}
