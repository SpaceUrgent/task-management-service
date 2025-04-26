package com.task.management.domain.project.port.in;

import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.projection.ProjectPreview;

import java.util.List;

public interface GetAvailableProjectsUseCase {
    List<ProjectPreview> getAvailableProjects(UserId actorId);
}
