package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.command.UpdateProjectCommand;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.ProjectId;

public interface UpdateProjectUseCase {
    void updateProject(UserId actorId, ProjectId projectId, UpdateProjectCommand command) throws UseCaseException;
}
