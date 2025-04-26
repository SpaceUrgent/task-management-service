package com.task.management.domain.project.port.in;

import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.projection.ProjectDetails;
import com.task.management.domain.project.model.ProjectId;

public interface GetProjectDetailsUseCase {
    ProjectDetails getProjectDetails(UserId actorId, ProjectId projectId) throws UseCaseException;
}
