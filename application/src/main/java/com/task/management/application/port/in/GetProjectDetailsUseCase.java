package com.task.management.application.port.in;

import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.ProjectDetails;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;

@Deprecated
public interface GetProjectDetailsUseCase {
    ProjectDetails getProjectDetails(UserId currentUser, ProjectId projectId) throws EntityNotFoundException, InsufficientPrivilegesException;
}
