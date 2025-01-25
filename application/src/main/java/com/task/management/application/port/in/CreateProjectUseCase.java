package com.task.management.application.port.in;

import com.task.management.application.model.Project;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.dto.CreateProjectDto;

public interface CreateProjectUseCase {
    Project createProject(UserId userId, CreateProjectDto createProjectDto);
}
