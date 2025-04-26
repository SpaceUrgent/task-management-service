package com.task.management.domain.project.port.in;

import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.application.command.CreateProjectCommand;

public interface CreateProjectUseCase {
    void createProject(UserId actorId, CreateProjectCommand command) throws UseCaseException;
}
