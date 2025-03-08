package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.port.in.command.CreateProjectCommand;

public interface CreateProjectUseCase {
    void createProject(ProjectUserId actorId, CreateProjectCommand command) throws UseCaseException;
}
