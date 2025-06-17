package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.ProjectId;

public interface GetProjectDetailsUseCase {
    ProjectDetails getProjectDetails(UserId actorId, ProjectId projectId) throws UseCaseException;
}
