package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;

public interface GetProjectUseCase {
    Project getProject(ProjectUserId actorId, ProjectId projectId) throws UseCaseException;
}
