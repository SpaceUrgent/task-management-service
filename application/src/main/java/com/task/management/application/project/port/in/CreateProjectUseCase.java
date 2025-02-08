package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.Project;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.port.in.command.CreateProjectCommand;

public interface CreateProjectUseCase {
    Project createProject(ProjectUserId actorId, CreateProjectCommand command) throws UseCaseException;
}
