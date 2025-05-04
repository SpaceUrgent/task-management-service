package com.task.management.application.project.port.in;

import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.domain.common.model.objectvalue.UserId;

import java.util.List;

public interface GetAvailableProjectsUseCase {
    List<ProjectPreview> getAvailableProjects(UserId actorId);
}
