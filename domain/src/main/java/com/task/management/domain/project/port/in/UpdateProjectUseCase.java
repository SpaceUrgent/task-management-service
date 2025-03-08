package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.port.in.command.UpdateProjectCommand;

public interface UpdateProjectUseCase {
    void updateProject(ProjectUserId actorId, ProjectId projectId, UpdateProjectCommand command) throws UseCaseException;
}
