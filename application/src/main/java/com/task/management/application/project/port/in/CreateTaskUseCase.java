package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.Task;
import com.task.management.application.project.port.in.command.CreateTaskCommand;

public interface CreateTaskUseCase {
    Task createTask(ProjectUserId actorId, CreateTaskCommand command) throws UseCaseException;
}
