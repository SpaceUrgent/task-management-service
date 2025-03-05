package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.model.Task;
import com.task.management.domain.project.port.in.command.CreateTaskCommand;

public interface CreateTaskUseCase {
    Task createTask(ProjectUserId actorId, CreateTaskCommand command) throws UseCaseException;;
}
