package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.Project;
import com.task.management.application.port.in.command.UpdateProjectCommand;
import com.task.management.application.project.model.ProjectUserId;

public interface UpdateProjectUseCase {
    Project updateProject(ProjectUserId actorId, UpdateProjectCommand command) throws UseCaseException;
}
