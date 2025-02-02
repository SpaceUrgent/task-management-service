package com.task.management.application.port.in;

import com.task.management.application.dto.ProjectDetailsDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;

public interface GetProjectDetailsUseCase {
    ProjectDetailsDTO getProjectDetails(UserId currentUser, ProjectId projectId) throws EntityNotFoundException, InsufficientPrivilegesException;
}
