package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.Project;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUserId;

public interface GetProjectUseCase {
    Project getProject(ProjectUserId actorId, ProjectId projectId) throws UseCaseException;
}
