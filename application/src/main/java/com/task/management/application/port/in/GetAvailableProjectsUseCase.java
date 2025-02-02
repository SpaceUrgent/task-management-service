package com.task.management.application.port.in;

import com.task.management.application.common.PageQuery;
import com.task.management.application.dto.ProjectDTO;
import com.task.management.application.model.Project;
import com.task.management.application.model.UserId;

import java.util.List;

public interface GetAvailableProjectsUseCase {
    List<ProjectDTO> getAvailableProjects(UserId userId, PageQuery page);
}
