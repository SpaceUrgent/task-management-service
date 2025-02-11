package com.task.management.application.project.port.in;

import com.task.management.application.project.model.ProjectPreview;
import com.task.management.application.project.model.ProjectUserId;

import java.util.List;

public interface GetAvailableProjectsUseCase {
    List<ProjectPreview> getAvailableProjects(ProjectUserId actorId);
}
