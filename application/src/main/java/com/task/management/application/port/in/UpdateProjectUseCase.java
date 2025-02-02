package com.task.management.application.port.in;

import com.task.management.application.dto.ProjectDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.application.dto.UpdateProjectDto;

public interface UpdateProjectUseCase {
    ProjectDTO updateProject(UserId currentUser, ProjectId projectId, UpdateProjectDto updateProjectDto) throws EntityNotFoundException, InsufficientPrivilegesException;
}
