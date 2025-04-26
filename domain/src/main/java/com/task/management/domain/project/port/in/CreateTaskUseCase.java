package com.task.management.domain.project.port.in;

import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.application.command.CreateTaskCommand;

public interface CreateTaskUseCase {
    void createTask(UserId actorId, ProjectId projectId, CreateTaskCommand command) throws UseCaseException;;
}
