package com.task.management.domain.project.port.in;

import com.task.management.domain.project.model.ProjectPreview;
import com.task.management.domain.project.model.ProjectUserId;

import java.util.List;

public interface GetAvailableProjectsUseCase {
    List<ProjectPreview> getAvailableProjects(ProjectUserId actorId);
}
