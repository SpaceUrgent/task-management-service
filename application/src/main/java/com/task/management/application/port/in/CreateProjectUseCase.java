package com.task.management.application.port.in;

import com.task.management.application.dto.ProjectDTO;
import com.task.management.application.model.UserId;
import com.task.management.application.dto.CreateProjectDto;

public interface CreateProjectUseCase {
    ProjectDTO createProject(UserId userId, CreateProjectDto createProjectDto);
}
