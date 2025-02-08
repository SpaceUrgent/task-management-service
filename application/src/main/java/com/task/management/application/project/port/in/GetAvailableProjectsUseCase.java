package com.task.management.application.project.port.in;

import com.task.management.application.project.model.Project;
import com.task.management.application.project.model.ProjectUserId;

import java.util.List;

public interface GetAvailableProjectsUseCase {
    List<Project> getAvailableProjects(ProjectUserId actorId);
}
