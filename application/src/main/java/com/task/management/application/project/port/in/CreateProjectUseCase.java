package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.command.CreateProjectCommand;
import com.task.management.domain.common.model.objectvalue.UserId;

public interface CreateProjectUseCase {
    void createProject(UserId actorId, CreateProjectCommand command) throws UseCaseException;
}
