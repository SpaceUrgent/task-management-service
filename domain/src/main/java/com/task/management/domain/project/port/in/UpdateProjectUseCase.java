package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.port.in.command.UpdateProjectCommand;
import com.task.management.domain.project.model.ProjectUserId;

public interface UpdateProjectUseCase {
    Project updateProject(ProjectUserId actorId, UpdateProjectCommand command) throws UseCaseException;
}
