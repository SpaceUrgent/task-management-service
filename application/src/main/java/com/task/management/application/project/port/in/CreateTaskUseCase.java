package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.command.CreateTaskCommand;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.ProjectId;

public interface CreateTaskUseCase {
    void createTask(UserId actorId, ProjectId projectId, CreateTaskCommand command) throws UseCaseException;;
}
